(defproject com.zensols.nlp/gate "0.1.0-SNAPSHOT"
  :description "Wrapper for Gate Annotation Utility"
  :url "https://github.com/plandes/Gate wraper"
  :license {:name "Apache License version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"
            :distribution :repo}
  :plugins [[lein-codox "0.10.1"]
            [org.clojars.cvillecsteele/lein-git-version "1.0.3"]]
  :codox {:metadata {:doc/format :markdown}
          :project {:name "Gate wraper"}
          :output-path "target/doc/codox"}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options ["-Xlint:unchecked"]
  :jar-exclusions [#".gitignore"]
  :exclusions [org.slf4j/slf4j-log4j12
               log4j/log4j
               ch.qos.logback/logback-classic]
  :dependencies [[org.clojure/clojure "1.8.0"]

                 ;; logging
                 [org.clojure/tools.logging "0.3.1"]

                 ;; command line
                 [com.zensols.tools/actioncli "0.0.12"]

                 ;; gate
                 [uk.ac.gate/gate-core "8.2"]]
  :profiles {:appassem {:aot :all}
             :dev
             {:jvm-opts
              ["-Dlog4j.configurationFile=test-resources/log4j2.xml" "-Xms4g" "-Xmx12g" "-XX:+UseConcMarkSweepGC"]
              :dependencies [[com.zensols/clj-append "1.0.4"]
                             [org.apache.logging.log4j/log4j-core "2.3"]
                             [org.apache.logging.log4j/log4j-api "2.3"]
                             [org.apache.logging.log4j/log4j-slf4j-impl "2.3"]
                             [org.apache.logging.log4j/log4j-jcl "2.3"]]}})
