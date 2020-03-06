package main

//go:generate go run -ldflags "${LD_FLAGS}" $GOFILE

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"path"
)

const metadataFile = "plugin-meta.json"

var (
	Package    string
	Version    string
	Executable string
	OutputDir  string
)

func main() {
	meta := make(map[string]interface{})
	meta["name"] = Package
	meta["version"] = Version
	meta["entrypoint"] = Executable
	data, err := json.MarshalIndent(meta, "", "    ")
	if err != nil {
		log.Fatal(err)
	}
	if err := ioutil.WriteFile(path.Join(OutputDir, metadataFile), data, 0644); err != nil {
		log.Fatal(err)
	}
	fmt.Println("Wrote", metadataFile)
}
