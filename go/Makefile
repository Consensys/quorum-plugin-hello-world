EXECUTABLE := "helloworld"
OUTPUT_DIR := "$(shell pwd)/build"
XC_ARCH := amd64
XC_OS := linux darwin
TARGET_DIRS := $(addsuffix -$(XC_ARCH), $(XC_OS))
PACKAGE ?= quorum-plugin-hello-world
VERSION ?= 1.0.0
LD_FLAGS := "-X main.Executable=${EXECUTABLE} -X main.Version=${VERSION} -X main.OutputDir=${OUTPUT_DIR} -X main.Package=${PACKAGE}"

.PHONY: $(OUTPUT_DIR)

default: clean build zip
	@echo Done!
	@ls -lha $(OUTPUT_DIR)/*

build: tools
	@mkdir -p $(OUTPUT_DIR)
	@LD_FLAGS=$(LD_FLAGS) go generate ./metadata
	@CGO_ENABLED=0 gox \
		-parallel=2 \
		-os="$(XC_OS)" \
		-arch="$(XC_ARCH)" \
		-ldflags="-s -w" \
		-output "$(OUTPUT_DIR)/{{.OS}}-{{.Arch}}/$(EXECUTABLE)" \
		.

zip: build $(TARGET_DIRS)

$(TARGET_DIRS):
	@zip -j -FS -q $(OUTPUT_DIR)/$@/$(PACKAGE)-$(VERSION).zip $(OUTPUT_DIR)/plugin-meta.json $(OUTPUT_DIR)/$@/*
	@shasum -a 256 $(OUTPUT_DIR)/$@/$(PACKAGE)-$(VERSION).zip | awk '{print $$1}' > $(OUTPUT_DIR)/$@/$(PACKAGE)-$(VERSION).zip.sha256sum

tools:
ifeq (, $(shell which gox))
	@GO111MODULE=off go get -u github.com/mitchellh/gox
endif

clean:
	@rm -rf $(OUTPUT_DIR)