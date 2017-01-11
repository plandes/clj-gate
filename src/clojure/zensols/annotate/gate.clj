(ns zensols.annotate.gate
  (:import [java.util HashSet]
           [gate Gate Factory]
           [gate.corpora DocumentImpl DocumentContentImpl]
           [gate.creole AnnotationSchema FeatureSchema])
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:require [zensols.actioncli.resource :as res]))

(defn initialize
  "Initialize the Gate system.  This is called when this namespace is loaded."
  []
  (res/register-resource :gate-home :system-file "gate.home"
                         :system-default
                         (-> (System/getProperty "user.home")
                             (io/file "Applications/Developer/GateDeveloper")
                             .getAbsolutePath))
  (let [gate-home (res/resource-path :gate-home)]
    (log/infof "initializing gate system with home: %s" gate-home)
    (-> (io/file gate-home "plugins")
        .getAbsolutePath
        (#(System/setProperty "gate.plugins.home" %)))
    (-> (io/file gate-home "gate.xml")
        .getAbsolutePath
        (#(System/setProperty "gate.site.config" %)))
    (Gate/init)
    gate-home))

(defn configure-plugins
  "Configure Gate plugins.  The no-arg default configures the `Alignment`
  plugin."
  ([]
   (configure-plugins ["Alignment"]))
  ([plugins]
   (doseq [plugin plugins]
     (let [plugin-dir (res/resource-path :gate-home
                                         (format "plugins/%s" plugin))]
       (-> (Gate/getCreoleRegister)
           (.registerDirectories (io/as-url plugin-dir)))))))

(defn- delete-recursively [fname]
  (let [func (fn [func f]
               (when (.isDirectory f)
                 (doseq [f2 (.listFiles f)]
                   (func func f2)))
               (io/delete-file f))]
    (func func (io/file fname))))

(defn- feature-map
  "Create a Gate `FeatureMap` from a Clojure (or any) map."
  ([]
   (feature-map nil))
  ([map]
   (let [fm (Factory/newFeatureMap)]
     (if map (.putAll fm map))
     fm)))

(defn annotation-schema-from-resource
  "Create a schema annotation from a schema the contents of **resource**.
  See [Gate docs](https://gate.ac.uk/sale/tao/splitch7.html#x11-1720007.11)).
  See [[annotation-schema]]."
  [resource]
  (->> {"xmlFileUrl" (->> resource io/resource io/as-url)}
       feature-map
       (Factory/createResource "gate.creole.AnnotationSchema")))

(defn annotation-schema
  "Create an annotation schema (i.e. entity) **label**.  If **options** is
  given provide additional feature schema metadata.
  See [[annotation-schema-from-resource]]."
  ([label]
   (annotation-schema label nil))
  ([label options]
   (->> (doto (AnnotationSchema.)
          (.setAnnotationName label)
          (.setFeatureSchemaSet
           (->> options
                (map (fn [{:keys [name value use options]
                           :or {use :default}}]
                       (let [usestr (cond (= use :default) "default"
                                          (= use :fixed) "fixed"
                                          true "")]
                         (FeatureSchema. name String
                                         (or value (format "%s-value" name))
                                         usestr
                                         (if options
                                           (HashSet. (set options)))))))
                HashSet.))))))

(defn create-document
  "Create a document with raw text.  You can annotate the returned document
  with [[annotate-document]]."
  ([text]
   (doto (DocumentImpl.)
     (.setContent (DocumentContentImpl. text))))
  ([text name]
   (doto (create-document text)
     (.setName name))))

(defn annotate-document
  "Annotate a document with entity **label** `type` from character
  position [**start** **end**) using additional entity metadata **features** in
  document **doc**."
  ([start end label doc]
   (annotate-document start end label {} doc))
  ([start end label features doc]
   (let [fmap (.getFeatures doc)
         anons (.getAnnotations doc)]
     (.add anons start end label (feature-map features))
     doc)))

(defn store-documents
  "Create a Gate data store that can be opened by the Gate GUI.  This creates a
  directory structure at **store-dir** and populates it **documents** that were
  create with [[create-document]].

  **Important**: this first deletes the **store-dir** directory if it exists.

Keys
----
* **corpus-name**: names the corpus and defaults to `zensos-gate-corpus`
* **resources**: resources (i.e. entities created with [[annotation-schema]])"
  [store-dir documents
   & {:keys [corpus-name resources]
      :or {corpus-name "zensols-gate-corpus"}}]
  (if (.exists store-dir)
    (delete-recursively store-dir))
  (let [store (Factory/createDataStore "gate.persist.SerialDataStore"
                                       (-> store-dir io/as-url .toString))]
    (try
      (let [corpus (Factory/newCorpus corpus-name)
            feats (doto (Factory/newFeatureMap)
                    (.put "transientSource" corpus))
            cd-feats (Factory/newFeatureMap)]
        (doall (map #(.add corpus %) documents))
        (->> (.adopt store corpus)
             (.sync store))
        (doseq [res resources]
          (->> (.adopt store res)
               (.sync store)))
        (log/infof "wrote store at: %s" store-dir))
      (finally (.close store)))))

(initialize)
