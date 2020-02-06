EXECUTABLE := "helloworld"
OUTPUT_DIR := "build"
XC_ARCH := amd64
XC_OS := linux darwin
TARGET_DIRS := $(addsuffix -$(XC_ARCH), $(XC_OS))

.PHONY: $(OUTPUT_DIR)

default: clean build zip
	@echo Done!
	@ls -lha $(OUTPUT_DIR)/*

build: tools
	@mkdir -p $(OUTPUT_DIR)
	@gox \
		-parallel=2 \
		-os="$(XC_OS)" \
		-arch="$(XC_ARCH)" \
		-ldflags="-s -w" \
		-output "$(OUTPUT_DIR)/{{.OS}}-{{.Arch}}/$(EXECUTABLE)" \
		.

zip: build $(TARGET_DIRS)

$(TARGET_DIRS):
	@zip -j -FS -q $(OUTPUT_DIR)/$@/quorum-plugin-hello-world-1.0.0.zip plugin-meta.json $(OUTPUT_DIR)/$@/*

tools:
ifeq (, $(shell which gox))
	@go get -u github.com/mitchellh/gox
endif

clean:
	@rm -rf $(OUTPUT_DIR)