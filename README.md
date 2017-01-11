# Gate Wraper

Wrapper for [Gate](https://gate.ac.uk) annotation natural language processing
utility.  This is a small wrapper that makes the following easier:
* Annotating Documents
* Create Store Documents
* Creating Annotation Schemas

This is handy if you automate annotation of documents and then want to bring
them into the GUI to do further annotation or just visually look at your
annotations.


## Obtaining

In your `project.clj` file, add:

[![Clojars Project](https://clojars.org/com.zensols.nlp/gate/latest-version.svg)](https://clojars.org/com.zensols.nlp/gate/)


## Documentation

API [documentation](https://plandes.github.io/clj-gate/codox/index.html).


## Usage

1. Download the Gate Developer application [here](https://gate.ac.uk).
2. By default the library looks for it in
   `~/Applications/Developer/GateDeveloper`.
3. Optionally set `gate.home` as system property as
   a [resource](https://github.com/plandes/clj-actioncli#resource-location).


## License

Copyright © 2016 Paul Landes

Apache License version 2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
