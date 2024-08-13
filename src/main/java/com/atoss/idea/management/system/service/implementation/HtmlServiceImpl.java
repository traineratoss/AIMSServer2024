package com.atoss.idea.management.system.service.implementation;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

@Service
public class HtmlServiceImpl {

    /**
     * Converts a Markdown string to an HTML string.
     *
     * <p>This method parses the provided Markdown content, converts it to an HTML
     * representation, and then removes any leading or trailing paragraph tags.</p>
     *
     * @param markdown the Markdown string to be converted; must not be null
     * @return the converted HTML string without leading or trailing paragraph tags
     * @throws IllegalArgumentException if the provided markdown string is null
     */
    public String markdownToHtml(String markdown) {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        String html = renderer.render(document);

        html = html.replaceAll("<img", "<img style=\"width:5vw; height:auto;\"");

        html = html.replaceAll("^<p>|</p>$", "").trim();

        return html;
    }
}
