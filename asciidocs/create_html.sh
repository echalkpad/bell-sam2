#!/bin/bash
asciidoctor -r asciidoctor-diagram -b html5 -a data-uri -a icons -a toc2 -a theme=flask $1
