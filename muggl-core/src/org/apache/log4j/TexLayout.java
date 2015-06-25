package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

public class TexLayout extends Layout {

    @Override
    public void activateOptions() {}

    @Override
    public String format(LoggingEvent event) {
	return event.getMessage().toString();
    }

    @Override
    public boolean ignoresThrowable() {
	return true;
    }

    @Override
    public String getHeader() {
	String header = "\\documentclass{article}\n" +
        	"\\usepackage[latin1]{inputenc}\n" +
        	"\\usepackage{a4,ngerman}\n" +
        	"\\usepackage{amsfonts}\n" +
        	"\\usepackage{exscale}\n" +
        	"\\setlength{\\oddsidemargin}{-0.5cm}\n" +
        	"\\setlength{\\paperwidth}{21cm}\n" +
        	"\\setlength{\\paperheight}{29.7cm}\n" +
        	"\\setlength{\\textwidth}{17cm}\n" +
        	"\\setlength{\\textheight}{25.0cm} % 26.5\n" +
        	"\\setlength{\\topmargin}{-1.5cm}\n" +
        	"\\setlength{\\headsep}{0cm}\n" +
        	"\\setlength{\\headheight}{0cm}\n" +
        	"\\setlength{\\parindent}{0cm}\n" +
        	"\\pagestyle{empty}\n" +
        	"\\begin{document}\n";
	return header;
    }
    
    @Override
    public String getFooter() {
	String footer = "\\end{document}\n";
	return footer;
    }


    
}
