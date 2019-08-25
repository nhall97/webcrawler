package com.frasercrossman.webcrawler;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebPageScraper {

  private WebClient wc;

  WebPageScraper() {
    wc = new WebClient();
    wc.setCssEnabled(false);
    wc.setAppletEnabled(false);
    wc.setJavaScriptEnabled(false);
  }

  public Set<URL> getInternalLinks(URL url) {
    Set<URL> internalLinks = new HashSet<>();

    try {
      Page page = wc.getPage(url);

      // Only proceed if the page is an HTML page
      if (page instanceof HtmlPage) {
        HtmlPage htmlPage = (HtmlPage) page;

        List<HtmlElement> anchorElements = htmlPage.getBody().getHtmlElementsByTagName("a");
        String href;
        URL newPageURL;

        for (int i = 0; i < anchorElements.size(); i++) {
          href = anchorElements.get(i).getAttribute("href");
          href = stripAnchorFromURL(href);

          // If link is local prepend root url protocol and hostname
          if (href.startsWith("/")) {
            href = url.getProtocol() + "://" + url.getHost() + href;
          }

          try {
            newPageURL = new URL(href);

            // Only add site if hosts match
            if (url.getHost().equalsIgnoreCase(newPageURL.getHost())) {
              internalLinks.add(newPageURL);
            }
          } catch (MalformedURLException e) {
          }
        }
      }
    } catch (IOException | FailingHttpStatusCodeException e) {
      e.printStackTrace();
    }

    return internalLinks;
  }

  public String stripAnchorFromURL(String url) {
    int anchorLocation = url.indexOf("#");

    if (anchorLocation != -1) {
      url = url.substring(0, anchorLocation);
    }

    return url;
  }
}
