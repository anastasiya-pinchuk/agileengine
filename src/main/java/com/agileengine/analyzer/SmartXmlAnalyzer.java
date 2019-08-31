package com.agileengine.analyzer;

import com.agileengine.exceptions.ElementNotFoundException;
import com.agileengine.exceptions.ParseDocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class SmartXmlAnalyzer {

    private static final String CHARSET_NAME = "utf8";
    private static final Comparator<Attribute> ATTRIBUTES_COMPARATOR_BY_KEY = Comparator.comparing(Attribute::getKey);

    private static Logger LOGGER = LoggerFactory.getLogger(SmartXmlAnalyzer.class);

    public void analyze(File originalFile, File modifiedFile, String targetElementId) {
        Element matchingElement = findMatchingElement(originalFile, modifiedFile, targetElementId);

        if (matchingElement == null) {
            System.out.println("Matching element not found");
        } else {
            System.out.println("Found XML Path: " + getElementPath(matchingElement));
        }
    }

    private Element findMatchingElement(File originalFile, File modifiedFile, String targetElementId) {
        Optional<Element> elementOptional = findElementById(originalFile, targetElementId);

        Element originalFileElement = elementOptional.orElseThrow(() -> new ElementNotFoundException(
                format("Element by id %s not found", targetElementId)));

        String tagName = originalFileElement.tagName();
        Elements modifiedFileElements = getDocument(modifiedFile).getElementsByTag(tagName);

        int maxMatchesCount = 0;
        Element matchingElement = null;
        List<Attribute> originalFileElementAttributes = originalFileElement.attributes().asList();

        for (Element modifiedFileElement : modifiedFileElements) {
            int matchesCount = getMatchesCount(originalFileElementAttributes, modifiedFileElement.attributes().asList());
            if (Objects.equals(originalFileElement.text(), modifiedFileElement.text())) {
                matchesCount++;
            }

            if (matchesCount > maxMatchesCount) {
                maxMatchesCount = matchesCount;
                matchingElement = modifiedFileElement;
            }
        }
        return matchingElement;
    }

    private Document getDocument(File htmlFile) {
        try {
            return Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            throw new ParseDocumentException(
                    format("Unable to parse document from file %s", htmlFile.getAbsolutePath()));

        }
    }

    private int getMatchesCount(List<Attribute> originalFileAttributes, List<Attribute> modifiedFileAttributes) {
        int matchesCount = 0;

        List<Attribute> sortedOriginalFileAttributes = originalFileAttributes.stream()
                .sorted(ATTRIBUTES_COMPARATOR_BY_KEY)
                .collect(Collectors.toList());
        List<Attribute> sortedModifiedFileAttributes = modifiedFileAttributes.stream()
                .sorted(ATTRIBUTES_COMPARATOR_BY_KEY)
                .collect(Collectors.toList());

        int originalIndex = 0;
        int modifiedIndex = 0;

        while (modifiedIndex < sortedModifiedFileAttributes.size()) {
            while (originalIndex < sortedOriginalFileAttributes.size()
                    && ATTRIBUTES_COMPARATOR_BY_KEY.compare(sortedOriginalFileAttributes.get(originalIndex), sortedModifiedFileAttributes.get(modifiedIndex)) < 0) {
                originalIndex++;
            }
            if (originalIndex == sortedOriginalFileAttributes.size()) {
                break;
            }

            if (Objects.equals(sortedOriginalFileAttributes.get(originalIndex).getKey(), sortedModifiedFileAttributes.get(modifiedIndex).getKey())) {
                if (Objects.equals(sortedOriginalFileAttributes.get(originalIndex).getValue(), sortedModifiedFileAttributes.get(modifiedIndex).getValue())) {
                    matchesCount++;
                }
                originalIndex++;
            }
            modifiedIndex++;
        }

        return matchesCount;
    }

    private Optional<Element> findElementById(File htmlFile, String targetElementId) {
        Document document = getDocument(htmlFile);
        return Optional.of(document.getElementById(targetElementId));
    }

    private String getElementPath(Element element) {
        Elements parents = element.parents();
        Collections.reverse(parents);
        parents.add(element);
        return parents.stream()
                .map(this::getParentIndexedTagName)
                .collect(Collectors.joining(" > "));
    }

    private String getParentIndexedTagName(Element e) {
        StringBuilder s = new StringBuilder();
        s.append(e.tagName());
        if (e.elementSiblingIndex() != 0) {
            s.append("[").append(e.elementSiblingIndex()).append("]");
        }
        return s.toString();
    }

}

