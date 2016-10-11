# Makefile for Bachelor Thesis

TARGET    := thesis.pdf

SOURCES   := $(wildcard include/*.tex)
RASTERIMG := $(wildcard images/*.png)
VECTORIMG := $(wildcard images/*.svg)
PLOTS     := $(wildcard plots/*.plot)


.PHONY: all clean distclean

all: $(TARGET)

$(TARGET): $(SOURCES) $(RASTERIMG) $(patsubst %.svg,%.pdf,$(VECTORIMG)) \
           $(patsubst %.plot,%.pdf,$(PLOTS))

%.pdf: %.tex %.bib
	pdflatex -file-line-error -interaction=nonstopmode $<
	bibtex $*
	pdflatex -file-line-error -interaction=nonstopmode $<
	pdflatex -file-line-error -interaction=nonstopmode $<
	pdflatex -file-line-error -interaction=nonstopmode $<

%.pdf: %.svg
	inkscape -A $@ $<

%.pdf: %.plot
	gnuplot < $<

clean:
	rm -f *.aux *.idx *.ind *.out *.toc *.log *.bbl *.blg *.brf
	rm -f include/*.aux images/*.pdf plots/*.pdf

distclean: clean
	rm -f *.pdf
