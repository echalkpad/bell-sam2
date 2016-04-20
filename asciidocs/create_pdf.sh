#!/bin/bash
asciidoctor-pdf -r asciidoctor-diagram -a pdf-style=themes/vennetics-theme.yml $1
