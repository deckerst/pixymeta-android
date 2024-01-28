/*
 * Copyright (c) 2014-2021 by Wen Yu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later version.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * 
 * Change History - most recent changes go on top of previous changes
 *
 * StringUtils.java
 *
 * Who   Date       Description
 * ====  =========  =====================================================
 * WY    29Apr2015  Renamed findAttribute() to getAttribute()
 * WY    09Apr2015  Added null check to findAttribute()
 * WY    03Mar2015  Added serializeToString() and serializeToByteArray()
 * WY    27Feb2015  Added findAttribute() and removeAttribute()
 * WY    23Jan2015  Initial creation - moved XML related methods to here
 */


package pixy.meta.string;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import pixy.meta.log.Logger;
import pixy.meta.log.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;

public class XMLUtils {
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);
		
	// Create an empty Document node
	public static Document createDocumentNode() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = null;
	    
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
        assert builder != null;

        return builder.newDocument();
	}

	public static Document createXML(byte[] xml) {
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//Get the DOM Builder
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		assert builder != null;

		//Load and Parse the XML document
		//document contains the complete XML as a Tree.
		Document document = null;
		try {
            document = builder.parse(new ByteArrayInputStream(xml));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

        return document;
	}
	
	public static Document createXML(String xml) {
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//Get the DOM Builder
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		assert builder != null;

		//Load and Parse the XML document
		//document contains the complete XML as a Tree.
		Document document = null;
		InputSource source = new InputSource(new StringReader(xml));
		try {
			document = builder.parse(source);			
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

        return document;
	}
	
	public static String escapeXML(String input) 
	{
		Iterator<Character> itr = StringUtils.stringIterator(input);
		StringBuilder result = new StringBuilder();		
		
		while (itr.hasNext())
		{
			Character c = itr.next();
			
			switch (c)
			{
				case '"':
					result.append("&quot;");
					break;
				case '\'':
					result.append("&apos;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '&':
					result.append("&amp;");
					break;
				default:
					result.append(c);
			}
		}
		
		return result.toString();
	}
	
	// Retrieve the first non-empty, non-null attribute value for the attribute name
	public static String getAttribute(Document doc, String tagName, String attribute) {
		// Sanity check
		if(doc == null || tagName == null || attribute == null)	return "";
		
		NodeList nodes = doc.getElementsByTagName(tagName);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			String attr = ((Element)nodes.item(i)).getAttribute(attribute);
			if(!StringUtils.isNullOrEmpty(attr))
				return attr;
		}
		
		return "";
	}
	
	public static void insertLeadingPI(Document doc, String target, String data) {
		Element element = doc.getDocumentElement();
	    ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
	    element.getParentNode().insertBefore(pi, element);
	}
	
	public static void insertTrailingPI(Document doc, String target, String data) {
		Element element = doc.getDocumentElement();
	    ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
	    element.getParentNode().appendChild(pi);
	}
		
	public static void printNode(Node node, String increment) {
		StringBuilder xmlTree = new StringBuilder();
		String indent = "";
		// Construct the XML tree
		print(node, indent, increment, xmlTree);
		// Log the XML tree
		LOGGER.info("\n{}", xmlTree);
	}
	
	private static void print(Node node, String indent, String increment, StringBuilder stringBuilder) {
		if(node != null) {
			if(indent == null) indent = "";  
			switch(node.getNodeType()) {
		        case Node.DOCUMENT_NODE: {
		            Node child = node.getFirstChild();
		            while(child != null) {
		            	print(child, indent, increment, stringBuilder);
		            	child = child.getNextSibling();
		            }
		            break;
		        } 
		        case Node.DOCUMENT_TYPE_NODE: {
		            DocumentType doctype = (DocumentType) node;
		            stringBuilder.append("<!DOCTYPE ").append(doctype.getName()).append(">\n");
		            break;
		        }
		        case Node.ELEMENT_NODE: { // Element node
		            Element ele = (Element) node;
		            stringBuilder.append(indent).append("<").append(ele.getTagName());
		            NamedNodeMap attrs = ele.getAttributes(); 
		            for(int i = 0; i < attrs.getLength(); i++) {
		                Node a = attrs.item(i);
		                stringBuilder.append(" ").append(a.getNodeName()).append("='").append(escapeXML(a.getNodeValue())).append("'");
		            }
		            stringBuilder.append(">\n");
	
		            Node child = ele.getFirstChild();
		            while(child != null) {
		            	print(child, indent + increment, increment, stringBuilder);
		            	child = child.getNextSibling();
		            }
	
		            stringBuilder.append(indent).append("</").append(ele.getTagName()).append(">\n");
		            break;
		        }
		        case Node.TEXT_NODE: {
		            Text textNode = (Text)node;
		            String text = textNode.getData().trim();
		            if (!text.isEmpty())
		                stringBuilder.append(indent).append(escapeXML(text)).append("\n");
		            break;
		        }
		        case Node.PROCESSING_INSTRUCTION_NODE: {
		            ProcessingInstruction pi = (ProcessingInstruction)node;
		            stringBuilder.append(indent).append("<?").append(pi.getTarget()).append(" ").append(pi.getData()).append("?>\n");
		            break;
		        }
		        case Node.ENTITY_REFERENCE_NODE: {
		            stringBuilder.append(indent).append("&").append(node.getNodeName()).append(";\n");
		            break;
		        }
		        case Node.CDATA_SECTION_NODE: { // Output CDATA sections
		            CDATASection cdata = (CDATASection)node;
		            stringBuilder.append(indent).append("<").append("![CDATA[").append(cdata.getData()).append("]]").append(">\n");
		            break;
		        }
		        case Node.COMMENT_NODE: {
		        	Comment c = (Comment)node;
		            stringBuilder.append(indent).append("<!--").append(c.getData()).append("-->\n");
		            break;
		        }
		        default:
		            LOGGER.error("Unknown node: " + node.getClass().getName());
		            break;
			}
		}
	}
	
	// Retrieve and remove the first non-empty, non-null attribute value for the attribute name
	public static void removeAttribute(Document doc, String tagName, String attribute) {
		NodeList nodes = doc.getElementsByTagName(tagName);

		for(int i = 0; i < nodes.getLength(); i++) {
			Element ele = (Element)nodes.item(i);
			String attr = ele.getAttribute(attribute);
			
			if(!StringUtils.isNullOrEmpty(attr)) {
				ele.removeAttribute(attribute);
				break;
			}
		}
	}
	
	public static byte[] serializeToByteArray(Document doc) throws IOException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new IOException("Unable to serialize XML document");
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		String encoding = doc.getInputEncoding();
		if(encoding == null) encoding = "UTF-8";
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result result = new StreamResult(out);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException("Unable to serialize XML document");
		}
		
		return out.toByteArray();
	}

	public static String serializeToString(Document doc) throws IOException {
		String encoding = doc.getInputEncoding();
		if(encoding == null) encoding = "UTF-8";
		
		return serializeToString(doc, encoding);
	}
	
	/**
	 * Serialize XML Document to string using Transformer
	 * 
	 * @param node the XML node (and the subtree rooted at this node) to be serialized
	 * @param encoding encoding for the XML document
	 * @return String representation of the Document
     */
	public static String serializeToString(Node node, String encoding) throws IOException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new IOException("Unable to serialize XML document");
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		DOMSource source = new DOMSource(node);
		StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException("Unable to serialize XML document");
		}		
	    writer.flush();
	    
        return writer.toString();
	}
	
	public static void showXML(Document document) {
		printNode(document, "     ");
	}
}
