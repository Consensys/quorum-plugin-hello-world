## Prerequisites

* Go 1.11.x
* Make

## How-to

* Run `make` to create plugin distribution zip files for different OSes. 
* Copy the zip file in the desired OS folder to Quorum plugin folder.
* Define `helloworld` block in the `providers` section of plugin settings JSON
   ```
   "helloworld": {
      "name":"quorum-plugin-hello-world",
      "version":"1.0.0",
      "config": "file://<path-to>/helloworld-plugin-config.json"
   }
   ```