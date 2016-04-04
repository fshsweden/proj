<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java" xmlns:lxslt="http://xml.apache.org/xslt" 
     xmlns:my-ext="Transform" xmlns:redirect="Transform$Redirect" 
     xmlns:o="urn:schemas-microsoft-com:office:office" extension-element-prefixes="my-ext redirect" xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="xalan java">
	
	<xsl:output method="text"/>
	<xsl:param name="outSource"/>

	
	<lxslt:component prefix="redirect" elements="write open close" functions="">
		<lxslt:script lang="javaclass" src="Transform$Redirect"/>
	</lxslt:component>
	
	<lxslt:component prefix="my-ext" elements="" functions="regImport upperFirst lowerFirst">
		<lxslt:script lang="javaclass" src="Transform"/>
	</lxslt:component>
	
<xsl:variable name="rawpackage" select='substring-after(my-ext:replace($outSource, "/", "."),"java.")'/>	
<xsl:variable name="package" select="substring($rawpackage, 1, string-length($rawpackage) - 1)"/>
	

<xsl:variable name="messageBases" select="java:java.util.Hashtable.new()"/>


<!--     ============================================== -->
<!--     					Main Entry Point						   -->
<!--     ============================================== -->

<xsl:template match="/MessageFiles">
   <!--xsl:message>********  <xsl:value-of select="$package"/></xsl:message -->
 
    <redirect:write select="concat($outSource,'MessageDictionary.java')">
package <xsl:value-of select="$package"/>;

import com.ev112.codeblack.common.messaging.MessageBinDecoder;
import com.ev112.codeblack.common.messaging.MessageJsonDecoder;
import com.ev112.codeblack.common.messaging.MessageInterface;
import com.ev112.codeblack.common.messaging.MessageWrapper;


<xsl:for-each select="MessageFile">
import <xsl:value-of select="@path"/>.*;</xsl:for-each>

public class MessageDictionary
{

	public static MessageInterface createAndInstanciateMessage( String pMessageString ) {
		int tMsgId = MessageJsonDecoder.extractMessageId(pMessageString);
		MessageInterface tMessage = getMessageInstance( tMsgId );
		if (tMessage != null) {
			tMessage.decode(new MessageJsonDecoder( pMessageString ));
		}
		return tMessage;
	}
	
	public static MessageInterface createAndInstanciateMessage( byte[] pMessageBuffer ) {
		int tMsgId = MessageBinDecoder.extractMessageId(pMessageBuffer);
		MessageInterface tMessage = getMessageInstance( tMsgId );
		if (tMessage != null) {
			tMessage.decode(new MessageBinDecoder( pMessageBuffer ));
		}
		return tMessage;
	}

	public static MessageInterface getMessageInstance( String pMessageIdStr ) {
	   if (pMessageIdStr == null) {
	     System.out.println("MessageDictionary, messageId string is &lt;null&gt; ");
	     return null;
	  }
	  try {
	  	int tMessageId = Integer.parseInt( pMessageIdStr );
	  	return getMessageInstance( tMessageId  );
	  }
	  catch( NumberFormatException e  ) {
	    System.out.println("MessageDictionary, invalid messageId format \"" + pMessageIdStr  +"\"");
	  }
	  return null;
	}

	public static MessageInterface getMessageInstance( int pMessageId ) {
		switch( pMessageId ) {
		  case MessageWrapper.MESSAGE_ID:
			return new com.ev112.codeblack.common.messaging.MessageWrapper();
  	 <xsl:for-each select="MessageFile">
  	 	<xsl:variable name="msgFileDoc">
			<xsl:element name="MessageFile">
			  	<xsl:attribute name="package"><xsl:value-of select="@path"/></xsl:attribute>
			   	<xsl:attribute name="xmlFile"><xsl:value-of select="@xmlFile"/></xsl:attribute>
			  	<xsl:apply-templates  select="document(@xmlFile)" mode="copyContents"/>
		      </xsl:element>
		</xsl:variable>
		<xsl:apply-templates select="xalan:nodeset($msgFileDoc)" mode="checkDuplicates"/>
		<xsl:apply-templates select="xalan:nodeset($msgFileDoc)" mode="messageInstance"/>
  	 </xsl:for-each>
  	   	default:
  	   		System.out.println("MessageDictionary, no message found for message with id: " + String.valueOf( pMessageId ));
  	   		return null;
  	   }
  	 }
 }
 
    </redirect:write>
</xsl:template>

<xsl:template match="*" mode="copyContents">
	<xsl:copy-of select='/*'/>
</xsl:template>




<xsl:template mode="messageInstance" match="MessageFile">
	<!--xsl:message>MESSAGEINSTANCE package <xsl:value-of select="@package"/> File: <xsl:value-of select="@file"/></xsl:message-->
	<xsl:for-each select="Messages/Message">
		<!--xsl:message>MESSAGE: Name:: <xsl:value-of select="@name"/></xsl:message-->
		case <xsl:value-of select="@name"/>.MESSAGE_ID:
			return new <xsl:value-of select="../../@package"/>.<xsl:value-of select="@name"/>();
	</xsl:for-each>
</xsl:template>


<xsl:template mode="checkDuplicates" match="MessageFile">	
	<xsl:variable name="msgBase" select="./Messages/@messageBase"/>
	<!-- xsl:message>CHECKDUPLICATES MessageBase: <xsl:value-of select="$msgBase"/> File: <xsl:value-of select="@xmlFile"/> </xsl:message -->
   <!-- Check that the message id is unique -->
   <xsl:variable name="dup" select="java:get($messageBases, string($msgBase))"/> 
    <xsl:if test="$dup">
   	<xsl:message> **** Error: duplicate message base: <xsl:value-of select="$msgBase"/> In File: <xsl:value-of select="@xmlFile"/> </xsl:message>
   	Error: Error: duplicate message id: <xsl:value-of select="$msgBase"/>  In File: <xsl:value-of select="@xmlFile"/>
    </xsl:if>  
    <xsl:if test="not($dup)">
      	 <xsl:variable name="dmy" select="java:put($messageBases, string($msgBase), string($msgBase))"/> 
    </xsl:if>
</xsl:template>


</xsl:stylesheet>
