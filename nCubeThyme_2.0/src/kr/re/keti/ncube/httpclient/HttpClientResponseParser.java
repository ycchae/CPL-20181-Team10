/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.ncube.httpclient;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * (Next version)
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class HttpClientResponseParser {
	public static String aeCreateParse(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource xmlSource = new InputSource();
		xmlSource.setCharacterStream(new StringReader(xml));
		Document document = builder.parse(xmlSource);
		
		String aei = "";
		
		NodeList aeIdNodeList = document.getElementsByTagName("aei");
		if (aeIdNodeList.getLength() > 0 && aeIdNodeList.item(0).getChildNodes().getLength() > 0) {
			Node aeIdNode = aeIdNodeList.item(0).getChildNodes().item(0);
			aei = aeIdNode.getNodeValue();
		}
		
		return aei;
	}
}