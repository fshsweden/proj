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
	
	
	
	<xsl:variable name="TypeTableDefinitions">
		<DataTypes>
		   <Type name="boolean" type="boolean" readMethod="readBoolean" readArrayMethod="readBooleanArray" />
		   <Type name="byte" type="byte"  readMethod="readByte"/>
		   <Type name="short" type="short"  readMethod="readShort"  readArrayMethod="readShortArray" />
		   <Type name="int" type="int" readMethod="readInt"  readArrayMethod="readIntArray" />
		   <Type name="long" type="long" readMethod="readLong"  readArrayMethod="readLongArray" />
		   <Type name="double" type="double" readMethod="readDouble"  readArrayMethod="readDoubleArray" />
		   <Type name="String" type="String" readMethod="readString"  readArrayMethod="readStringArray" />
		   <Type name="byte[]" type="byte[]" readMethod="readBytes"  readArrayMethod="readBytesArray" />
		</DataTypes>
	</xsl:variable>
      <xsl:variable name="typeTable" select="xalan:nodeset($TypeTableDefinitions)/DataTypes"/>

	<xsl:variable name="services" select="document('../definitions/mts-services.xml')"/>

	

<xsl:variable name="messageIdentities" select="java:java.util.Hashtable.new()"/>

<xsl:variable name="constantPrefixValue" select="/Messages/ConstantGroups/@prefix"/>
<xsl:variable name="constantPrefix" select="concat($constantPrefixValue,'Constants')"/>


<!--     ============================================== -->
<!--     					Main Entry Point						   -->
<!--     ============================================== -->

<xsl:template match="/Messages">
   <xsl:for-each select="Message">
   		<xsl:variable name="msgId" select="position()"/>
   	       <xsl:apply-templates mode="generateMessage" select=".">
   	       	<xsl:with-param name="messageId" select="$msgId"/>
   	       </xsl:apply-templates>
   	       
   	        <!-- xsl:apply-templates mode="generateMessageReadOnlyInterface" select="."/ -->
    </xsl:for-each>  
    <xsl:apply-templates mode="generateConstantGroups" select="."/>
</xsl:template>


<!--     ==================================================== -->
<!--     			      Generate ConstantGroups						   -->
<!--     ===================================================== -->

<xsl:template mode="generateConstantGroups" match="Messages">
<xsl:if test="ConstantGroups">
<redirect:write select="concat($outSource,$constantPrefix,'.java')">
package <xsl:value-of select="$package"/>;

public class <xsl:value-of select="$constantPrefix"/>
{
	<xsl:for-each select="ConstantGroups/Group">
	public static enum <xsl:value-of select="@name"/> {
		<xsl:for-each select="Constant">
		<xsl:value-of select="@value"/><xsl:if test="not(position()=last())">, </xsl:if></xsl:for-each>
	};
	</xsl:for-each>
}
</redirect:write>
</xsl:if>
</xsl:template>


<!--     ============================================== -->
<!--     			      Import templates							   -->
<!--     ============================================== -->

<xsl:template mode="addImports" match="Imports">
	// Add XML defined imports
	<xsl:for-each select="Import">
import <xsl:value-of select="@path"/>;</xsl:for-each>
</xsl:template>


<!--     ============================================== -->
<!--     			      Generate Message Class				   -->
<!--     ============================================== -->
<xsl:template mode="generateMessage" match="Message">
   <xsl:param name="messageId"/>
   

  
  
<xsl:variable name="constPrefix" select="../@prefix"/>
<redirect:write select="concat($outSource,@name,'.java')">
package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.ev112.codeblack.common.messaging.MessageJsonDecoder;
import com.ev112.codeblack.common.messaging.MessageJsonEncoder;
import com.ev112.codeblack.common.messaging.MessageBinDecoder;
import com.ev112.codeblack.common.messaging.MessageBinEncoder;
import com.ev112.codeblack.common.messaging.MessageInterface;
import com.ev112.codeblack.common.messaging.MessageAux;
import com.ev112.codeblack.common.messaging.MessageWrapper;

<xsl:if test="@dbObject">
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import org.hibernate.annotations.IndexColumn;
</xsl:if>


<xsl:apply-templates mode="addImports" select="../Imports"/>
<xsl:apply-templates mode="addImports" select="./Imports"/>

<xsl:apply-templates mode="dbAnnotationClass" select="."/>
public class <xsl:value-of select="@name"/> <xsl:if test="@extends"> extends <xsl:value-of select="@extends"/> </xsl:if> implements <xsl:if test="@service">MessageServiceInterface  </xsl:if>  <xsl:if test="not(@service)">MessageInterface  </xsl:if> <xsl:if test="@apiMessage='Response' or @apiMessage='Broadcast'">, MtsApiOutMessageInterface</xsl:if>
{
	<xsl:apply-templates mode="declareConstants" select=".">
		<xsl:with-param name="messageId" select="$messageId"/>
	</xsl:apply-templates>
	<xsl:apply-templates mode="declareAttributes" select="."/>
	<xsl:apply-templates mode="declareConstructors" select="."/>
	<xsl:if test="@service">
		<xsl:apply-templates mode="declareServiceInterface" select="."/>
	</xsl:if>
	<xsl:apply-templates mode="declareGettersSetters" select="."/>
	<xsl:apply-templates mode="declareMessageIfMethods" select=".">
		<xsl:with-param name="messageId" select="$messageId"/>
	</xsl:apply-templates>
	<xsl:apply-templates mode="declareFormater" select="."/>
	<xsl:apply-templates mode="declareCloneMethod" select="."/>	
	<xsl:apply-templates mode="declareTree" select="."/>
	<xsl:apply-templates mode="applyCode" select="."/>
}

</redirect:write>
</xsl:template>



<!--     ============================================== -->
<!--     			Declare Constants           					   -->
<!--     ============================================== -->

<xsl:template mode="declareConstants" match="Message">
   <xsl:param name="messageId"/>
   
   public static final int MESSAGE_ID = <xsl:value-of select="$messageId"/>;
<xsl:if test="@dbAutoKey">
	private Long __mHibernateAutoKey; </xsl:if>
   <xsl:for-each select="Constants/Constant">
   <xsl:if test="@type='String'">public static final <xsl:value-of select="@type"/>  <xsl:text> </xsl:text> <xsl:value-of select="@name"/> = "<xsl:value-of select="@value"/>"; 
   </xsl:if>
   <xsl:if test="not(@type='String')">public static final <xsl:value-of select="@type"/> <xsl:text> </xsl:text> <xsl:value-of select="@name"/> = <xsl:value-of select="@value"/>; 
   </xsl:if>
   </xsl:for-each>
</xsl:template>




<!--     ============================================== -->
<!--     			Declare Message Attributes					   -->
<!--     ============================================== -->

<xsl:template mode="declareAttributes" match="Message">
   protected volatile String mMessageStringCached = null;
   protected volatile byte[]  mMessageBytesCached=null;
   
   <xsl:if test="@service">
   <xsl:variable name="serviceCode" select="@service"/>
   <xsl:if test="$services/MtsServices/Service[@code=$serviceCode]/@defaultRoutingKey">
   private Integer _mRoutingKey = new Integer( <xsl:value-of select="$services/MtsServices/Service[@code=$serviceCode]/@defaultRoutingKey"/> );
   </xsl:if>
   <xsl:if test="not($services/MtsServices/Service[@code=$serviceCode]/@defaultRoutingKey)">
   private Integer _mRoutingKey = null;
   </xsl:if>
</xsl:if>
   <xsl:for-each select="Attribute">
   	<xsl:if test="@constantGroup">private <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/> m<xsl:value-of select="my-ext:upperFirst (@name)"/>;</xsl:if>
   	<xsl:if test="not(@constantGroup)">
      <xsl:variable name="dataType" select="@type"/>
      <xsl:if test="$typeTable/Type[@name=$dataType]">
	private <xsl:value-of select="$typeTable/Type[@name=$dataType]/@type"/><xsl:if test="@array">[]</xsl:if>  m<xsl:value-of select="my-ext:upperFirst (@name)"/>;</xsl:if>
	<xsl:if test="not($typeTable/Type[@name=$dataType])">
	private <xsl:if test="@array">List&lt;<xsl:value-of select="$dataType"/>&gt;</xsl:if><xsl:if test="not(@array)"><xsl:value-of select="@type"/></xsl:if> m<xsl:value-of select="my-ext:upperFirst (@name)"/>;</xsl:if></xsl:if>
   </xsl:for-each>   
</xsl:template>


<!--     ============================================== -->
<!--     			Declare Message Constructors				   -->
<!--     ============================================== -->

<xsl:template mode="declareConstructors" match="Message">

public <xsl:value-of select="@name"/>()
{
 <xsl:if test="@extends">super();</xsl:if>
}

public  <xsl:value-of select="@name"/>(String pMessageString ) {
   MessageJsonDecoder tDecoder = new MessageJsonDecoder( pMessageString );
   this.decode( tDecoder );
}



public  <xsl:value-of select="@name"/>(byte[]  pMessageByteArray ) {
   MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
   this.decode( tDecoder );
}

 <!--
<xsl:if test="count(Attribute) > 0">
public  <xsl:value-of select="@name"/>( 
<xsl:for-each select="Attribute">
    <xsl:if test="@constantGroup"><xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/> p<xsl:value-of select="my-ext:upperFirst (@name)"/><xsl:if test="not(position()=last())">, 
       </xsl:if>
       </xsl:if>
    <xsl:if test="not(@constantGroup)">
    <xsl:variable name="dataType" select="@type"/>
       <xsl:if test="$typeTable/Type[@name=$dataType]">
       <xsl:value-of select="$typeTable/Type[@name=$dataType]/@type"/><xsl:if test="@array">[]</xsl:if>  p<xsl:value-of select="my-ext:upperFirst (@name)"/><xsl:if test="not(position()=last())">, 
       </xsl:if>
       </xsl:if>
	 <xsl:if test="not($typeTable/Type[@name=$dataType])">
	<xsl:if test="@array">Vector&lt;<xsl:value-of select="$dataType"/>&gt;</xsl:if><xsl:if test="not(@array)"><xsl:value-of select="$dataType"/></xsl:if> p<xsl:value-of select="my-ext:upperFirst (@name)"/> <xsl:if test="not(position()=last())">, 
	</xsl:if>
	</xsl:if>
	</xsl:if>
</xsl:for-each> )
{
 <xsl:if test="@extends">super();</xsl:if>
 <xsl:for-each select="Attribute">
	set<xsl:value-of select="my-ext:upperFirst (@name)"/>(  p<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:for-each>
 }
</xsl:if>
-->
</xsl:template>

<!--     ============================================== -->
<!--     			DeclareService Interface       				   -->
<!--     ============================================== -->

<xsl:template mode="declareServiceInterface" match="Message">
public Integer getRoutingKey() 
{
	return _mRoutingKey;
}

public void setRoutingKey( int pKeyValue ) {
   _mRoutingKey = new Integer( pKeyValue );
}

public int getService() {
	<xsl:variable name="serviceCode" select="@service"/>
	<xsl:if test="$services/MtsServices/Service[@code=$serviceCode]">
	return  MtsServices.<xsl:value-of select="$serviceCode"/>;</xsl:if>
	<xsl:if test="not($services/MtsServices/Service[@code=$serviceCode])">
	<xsl:message>*** Error message service <xsl:value-of select="$serviceCode"/> is not defined in file mts-services.xml</xsl:message>
	*** Error message service <xsl:value-of select="$serviceCode"/> is not defined in file mts-services.xml
	</xsl:if>
}


</xsl:template>

<!--     ============================================== -->
<!--     			Apply code written in XSL very uggly             	   -->
<!--     ============================================== -->

<xsl:template mode="applyCode" match="Message">
	<xsl:value-of select="code"/>
</xsl:template>


<!--     ============================================== -->
<!--     			Declare DeclareGettersSetters                  	   -->
<!--     ============================================== -->


<xsl:template mode="declareGettersSetters" match="Message">

<xsl:for-each select="Attribute"> 
    <xsl:if test="not(@noGetterSetter='true')">
       
    <xsl:if test="@constantGroup">
    		  <xsl:apply-templates mode="declareConstantGetterSetter" select="."/>
    </xsl:if>
    <xsl:if test="not(@constantGroup)">

    <xsl:variable name="dataType" select="@type"/>
       <xsl:if test="$typeTable/Type[@name=$dataType]">
           <xsl:apply-templates mode="declareNativeGetterSetter" select="."/>
       </xsl:if>
       <xsl:if test="not($typeTable/Type[@name=$dataType])">
             <xsl:apply-templates mode="declareMessageGetterSetter" select="."/>
       </xsl:if>
        </xsl:if>
      </xsl:if>
</xsl:for-each>
</xsl:template>

<xsl:template mode="declareConstantGetterSetter" match="Attribute">
public void set<xsl:value-of select="my-ext:upperFirst (@name)"/>( <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/> p<xsl:value-of select="my-ext:upperFirst (@name)"/> ) {
	  m<xsl:value-of select="my-ext:upperFirst (@name)"/>  = p<xsl:value-of select="my-ext:upperFirst (@name)"/> ;
} 

<xsl:apply-templates mode="dbAnnotationAttribute" select="."/>
public  <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/>  get<xsl:value-of select="my-ext:upperFirst (@name)"/>() {
  return  m<xsl:value-of select="my-ext:upperFirst (@name)"/> ;
}

</xsl:template>




<xsl:template mode="declareNativeGetterSetter" match="Attribute">

<xsl:variable name="dataType" select="@type"/>
<xsl:variable name="type" select="$typeTable/Type[@name=$dataType]/@type"/>

public void set<xsl:value-of select="my-ext:upperFirst (@name)"/>( <xsl:value-of select="$type"/><xsl:if test="@array">[]</xsl:if> p<xsl:value-of select="my-ext:upperFirst (@name)"/> ) {
   m<xsl:value-of select="my-ext:upperFirst (@name)"/>  = p<xsl:value-of select="my-ext:upperFirst (@name)"/> ;
   synchronized( this) { 
   	mMessageStringCached = null;
   	mMessageBytesCached = null;
   }
}

<xsl:apply-templates mode="dbAnnotationAttribute" select="."/>
public  <xsl:value-of select="$type"/><xsl:if test="@array">[]</xsl:if> get<xsl:value-of select="my-ext:upperFirst (@name)"/>() {
  return  m<xsl:value-of select="my-ext:upperFirst (@name)"/> ;
}
</xsl:template>




<xsl:template mode="declareMessageGetterSetter" match="Attribute">
<xsl:variable name="dataType" select="@type"/>

<xsl:if test="@array">
public void set<xsl:value-of select="my-ext:upperFirst (@name)"/>( List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="my-ext:upperFirst (@name)"/> ) {
      if (p<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
        m<xsl:value-of select="my-ext:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageStringCached = null;
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  p<xsl:value-of select="my-ext:upperFirst (@name)"/>.size();
      
      if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="my-ext:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( tSize + 1 );
	  

	m<xsl:value-of select="my-ext:upperFirst (@name)"/> .addAll( p<xsl:value-of select="my-ext:upperFirst (@name)"/> );

	synchronized( this) { 
		mMessageStringCached = null;
       	mMessageBytesCached = null;
       }
}

public void set<xsl:value-of select="my-ext:upperFirst (@name)"/>( <xsl:value-of select="$dataType"/>[] p<xsl:value-of select="my-ext:upperFirst (@name)"/> ) {
      if (p<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
        m<xsl:value-of select="my-ext:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageStringCached = null;
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  p<xsl:value-of select="my-ext:upperFirst (@name)"/>.length;
      
      if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="my-ext:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( tSize + 1 );
	  
	for( int i = 0; i &lt; tSize; i++ ) {
	    m<xsl:value-of select="my-ext:upperFirst (@name)"/> .add( p<xsl:value-of select="my-ext:upperFirst (@name)"/>[i]);
	}
	synchronized( this) { 
	 	mMessageStringCached = null;
       	mMessageBytesCached = null;
       }
}

	
public void add<xsl:value-of select="my-ext:upperFirst (@name)"/>( List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="my-ext:upperFirst (@name)"/> ) {

      if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="my-ext:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;();
	  
	m<xsl:value-of select="my-ext:upperFirst (@name)"/> .addAll(  p<xsl:value-of select="my-ext:upperFirst (@name)"/> );
	synchronized( this) { 
		mMessageStringCached = null;
       	mMessageBytesCached = null;
       }
}

<xsl:apply-templates mode="dbAnnotationAttribute" select="."/>
public  List&lt;<xsl:value-of select="$dataType"/>&gt; get<xsl:value-of select="my-ext:upperFirst (@name)"/>() {
	
	if (m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null)
	  return null;
	
	List&lt;<xsl:value-of select="$dataType"/>&gt; tList = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( m<xsl:value-of select="my-ext:upperFirst (@name)"/>.size() );
	tList.addAll(  m<xsl:value-of select="my-ext:upperFirst (@name)"/> );
	return tList;
}

</xsl:if>

<xsl:if test="not(@array)">
<xsl:apply-templates mode="dbAnnotationAttribute" select="."/>
public  <xsl:value-of select="$dataType"/>  get<xsl:value-of select="my-ext:upperFirst (@name)"/>() {
  return m<xsl:value-of select="my-ext:upperFirst (@name)"/>;
}

public  void set<xsl:value-of select="my-ext:upperFirst (@name)"/>(<xsl:value-of select="$dataType"/>  p<xsl:value-of select="my-ext:upperFirst (@name)"/>) {
  m<xsl:value-of select="my-ext:upperFirst (@name)"/> = p<xsl:value-of select="my-ext:upperFirst (@name)"/>;
}
	
</xsl:if>



</xsl:template>



<!--     ============================================== -->
<!--     			Declare MessageIf methods                       	   -->
<!--     ============================================== -->

<xsl:template mode="declareMessageIfMethods" match="Message">
	<xsl:param name="messageId"/>
	<xsl:apply-templates mode="declareMsgIdMethods" select=".">
		<xsl:with-param name="messageId" select="$messageId"/>
	</xsl:apply-templates>
	<xsl:apply-templates mode="declareMsgCodecMethods" select="."/>

<xsl:if test="@dbObject">@Transient </xsl:if>
    	public String messageToString() {
    	    synchronized( this) { 
    	    <xsl:if test="@extends">
    	        if ((mMessageStringCached  != null)  &amp;&amp; (super.mMessageStringCached  != null)) {
    	   </xsl:if>
    	    <xsl:if test="not(@extends)">
		   if (mMessageStringCached  != null)  {
	    </xsl:if>
      	       return mMessageStringCached ;
    	    } else {
    		MessageJsonEncoder tEncoder = new MessageJsonEncoder();
    		this.encode( tEncoder );
    		mMessageStringCached  =  tEncoder.toString();
    		return mMessageStringCached;    	
    	    }
    	    }
    	}

<xsl:if test="@dbObject">@Transient </xsl:if>    	
    	public byte[] messageToBytes() {
    	   synchronized( this) { 
    	   <xsl:if test="@extends">
    	        if ((mMessageBytesCached != null)  &amp;&amp; (super.mMessageBytesCached != null)) {
    	   </xsl:if>
    	    <xsl:if test="not(@extends)">
		   if (mMessageBytesCached != null)  {
	    </xsl:if>
    	     return mMessageBytesCached ;
    	   } else {
    	   	MessageBinEncoder tEncoder = new MessageBinEncoder();
    	  	 this.encode( tEncoder );
    	   	 mMessageBytesCached  =  tEncoder.getBytes();
		return mMessageBytesCached;
	  }
	  }
      }
</xsl:template>


<!--     ==================================================== -->
<!--     			Declare MessageIf ID methodsmethods                       	  -->
<!--     ==================================================== -->
<xsl:template mode="declareMsgIdMethods" match="Message">
   <xsl:param name="messageId"/>

   
   <!-- Get Message Id -->
   <xsl:if test="not($messageId)">
   	<xsl:message>**** Error: message <xsl:value-of select="@name"/> has not a messageId attribute defined!</xsl:message>
  </xsl:if>
   
<xsl:if test="@dbAutoKey">
@Id
@GeneratedValue
public Long getHiberAutoKey() {
	return __mHibernateAutoKey; 
}

public void setHiberAutoKey( Long pId ) {
	__mHibernateAutoKey = pId;
} </xsl:if>
  
   <!-- Check that the message id is unique -->
   <xsl:variable name="dup" select="java:get($messageIdentities, string($messageId))"/> 
    <xsl:if test="$dup">
   	<xsl:message> **** Error: duplicate message id: <xsl:value-of select="$messageId"/> Message: <xsl:value-of select="@name"/> </xsl:message>
   	Error: Error: duplicate message id: <xsl:value-of select="$messageId"/> Message: <xsl:value-of select="@name"/> 
   </xsl:if>  
    <xsl:if test="not($dup)">
      	 <xsl:variable name="dmy" select="java:put($messageIdentities, string($messageId), string($messageId))"/> 
    </xsl:if>
   
<xsl:if test="@dbObject">@Transient </xsl:if>
public String getMessageName() {
   return "<xsl:value-of select="@name"/>";
}
<xsl:if test="@dbObject">@Transient </xsl:if>
public String getFullMessageName() {
    return "<xsl:value-of select="$package"/>.<xsl:value-of select="@name"/>";
}
 <xsl:if test="@dbObject">@Transient </xsl:if>
 public int getMessageId() {
   	return   <xsl:value-of select="$messageId"/>;
  }
   
</xsl:template>


<!--     ==================================================== -->
<!--     			Declare MessageIf encode/decode methods               	  -->
<!--     ==================================================== -->


<xsl:template mode="declareMsgCodecMethods" match="Message">

  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		<xsl:if test="@extends">super.encode( pEncoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	    <xsl:if test="@constantGroup">
	  	 /**
	    	  * Encode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
	        */
	        if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
	          pEncoder.add((String) null);
	        } else {
	  	 	pEncoder.add(  m<xsl:value-of select="my-ext:upperFirst (@name)"/>.toString() );
	  	 }</xsl:if>
	  	 
	   <xsl:if test="not(@constantGroup)">
	    /**
	    * Encode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="encoderArrayBin" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="encoderSingleBin" select="."/>
	  </xsl:if>
	  </xsl:if>
	</xsl:for-each>
  }

  public void encode( MessageJsonEncoder pEncoder ) {
    encode( pEncoder, false );
  }

  public void encode( MessageJsonEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
	           pEncoder.add("messageId", getMessageId());
	        }
  		<xsl:if test="@extends">super.encode( pEncoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	  	 <xsl:if test="@constantGroup">
	  	  /**
	    	  * Encode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
	        */
	        if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
	          pEncoder.add("<xsl:value-of select='@name'/>", (String) null);
	        } else {
	  	 	pEncoder.add(  "<xsl:value-of select='@name'/>", m<xsl:value-of select="my-ext:upperFirst (@name)"/>.toString() );
	  	 }</xsl:if>

	  	 
	  <xsl:if test="not(@constantGroup)">
	    /**
	    * Encode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="encoderArrayJson" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="encoderSingleJson" select="."/>
	  </xsl:if>
	  </xsl:if>
	</xsl:for-each>
  }
  

  public void decode( MessageBinDecoder pDecoder) {
     decode( pDecoder, false );
  }
  
  public void decode( MessageBinDecoder pDecoder, boolean pIsExtensionInvoked ) {
      String tStr = null;
      int tSize = 0;
      
     if (!pIsExtensionInvoked) {    
      pDecoder.readInt();	// Read Message Id 
     }

	   <xsl:if test="@extends">super.decode( pDecoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	    <xsl:if test="@constantGroup">
	    	 /**
	    	  * Decode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
	        */
	        tStr = pDecoder.readString();
	        if (tStr == null) {
	           m<xsl:value-of select="my-ext:upperFirst (@name)"/> = null;
	         } else {
		  	m<xsl:value-of select="my-ext:upperFirst (@name)"/> = <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/>.valueOf(tStr);
		  }
	     </xsl:if>
	     <xsl:if test="not(@constantGroup)">
	   /**
	    * Decoding Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="decoderArrayBin" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="decoderSingleBin" select="."/>
	  </xsl:if>
	  </xsl:if>
	</xsl:for-each>
  }
  

  public void decode( MessageJsonDecoder pDecoder) {
     decode( pDecoder, false );
  }

    public void decode( MessageJsonDecoder pDecoder, boolean pIsExtensionInvoked ) {
    	    String tStr = null;
    	    int tSize = 0;
    	   
    	   try {
	   if (!pIsExtensionInvoked) {
    	     pDecoder.readInt("messageId");	// Read Message Id 
           }

    	   <xsl:if test="@extends">super.decode( pDecoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	    <xsl:if test="@constantGroup">
	    	 /**
	    	  * Decode Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
	        */
		  tStr = pDecoder.readString("<xsl:value-of select='@name'/>" );
	        if (tStr == null) {
	           m<xsl:value-of select="my-ext:upperFirst (@name)"/> = null;
	         } else {
		  	m<xsl:value-of select="my-ext:upperFirst (@name)"/> = <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/>.valueOf(tStr);
		  }	    </xsl:if>
	     <xsl:if test="not(@constantGroup)">

	   /**
	    * Decoding Attribute: m<xsl:value-of select="my-ext:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="decoderArrayJson" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="decoderSingleJson" select="."/>
	  </xsl:if>
	  </xsl:if>

	</xsl:for-each>
	}
	catch( Exception e ) {
	   e.printStackTrace();
	   throw new RuntimeException("Failed to Json encode message, reason: " + e.getMessage());
	}
  }
  
  
</xsl:template>

<!-- ++++++++++++++++++++++++++++++++++++++++++
                           Decode methods 
 ++++++++++++++++++++++++++++++++++++++++++++ -->
 
 <xsl:template mode="decoderSingleBin" match="Attribute">
 <xsl:variable name="dataType" select="@type"/>

  <xsl:if test="$typeTable/Type[@name=$dataType]">
     m<xsl:value-of select="my-ext:upperFirst (@name)"/> = pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readMethod"/>(); </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
     m<xsl:value-of select="my-ext:upperFirst (@name)"/> = (<xsl:value-of select="$dataType"/>) pDecoder.readMessage( <xsl:value-of select="$dataType"/>.class );  </xsl:if>
 </xsl:template>
 
  <xsl:template mode="decoderSingleJson" match="Attribute">
 <xsl:variable name="dataType" select="@type"/>

  <xsl:if test="$typeTable/Type[@name=$dataType]">
     m<xsl:value-of select="my-ext:upperFirst (@name)"/> = pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readMethod"/>("<xsl:value-of select='@name'/>");
  </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
      //m<xsl:value-of select="my-ext:upperFirst (@name)"/> = new <xsl:value-of select="$dataType"/>();
      m<xsl:value-of select="my-ext:upperFirst (@name)"/> = ( <xsl:value-of select="$dataType"/>) pDecoder.readMessage( "<xsl:value-of select='@name'/>", <xsl:value-of select="$dataType"/>.class );  
  </xsl:if>
 </xsl:template>
 
 

<xsl:template mode="decoderArrayBin" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
     m<xsl:value-of select="my-ext:upperFirst (@name)"/> =  pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readArrayMethod"/>();  </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
    m<xsl:value-of select="my-ext:upperFirst (@name)"/> = (List&lt;<xsl:value-of select="$dataType"/>&gt;) pDecoder.readMessageArray( <xsl:value-of select="$dataType"/>.class ); </xsl:if>
</xsl:template>


<xsl:template mode="decoderArrayJson" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
      m<xsl:value-of select="my-ext:upperFirst (@name)"/> = pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readArrayMethod"/>("<xsl:value-of select='@name'/>"); </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
      m<xsl:value-of select="my-ext:upperFirst (@name)"/> = (List&lt;<xsl:value-of select="$dataType"/>&gt;)  pDecoder.readMessageArray( "<xsl:value-of select='@name'/>", <xsl:value-of select="$dataType"/>.class ); </xsl:if>
</xsl:template>

<!-- +++++++++++++++++++++++++++++++++++++++++++++
                           Enoder methods 
++++++++++++++++++++++++++++++++++++++++++++++++ -->
<xsl:template mode="encoderArrayBin" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
     pEncoder.add( m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
     pEncoder.addMessageArray( m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:if>
</xsl:template>



<xsl:template mode="encoderArrayJson" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
     pEncoder.add( "<xsl:value-of select='@name'/>", m<xsl:value-of select="my-ext:upperFirst (@name)"/> ); </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
      pEncoder.addMessageArray( "<xsl:value-of select='@name'/>", m<xsl:value-of select="my-ext:upperFirst (@name)"/> ); </xsl:if>
</xsl:template>










<xsl:template mode="encoderSingleBin" match="Attribute">
	  pEncoder.add(  m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:template>

<xsl:template mode="encoderSingleJson" match="Attribute">
	  pEncoder.add("<xsl:value-of select='@name'/>",  m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:template>

<xsl:template mode="formatNative" match="Attribute">
 
   <xsl:if test="@array">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="my-ext:upperFirst (@name)"/>[]: ");
     tSB.append( MessageAux.format( m<xsl:value-of select="my-ext:upperFirst (@name)"/> ));
     tSB.append("\n");</xsl:if>
   <xsl:if test="not(@array)">
     <xsl:if test="@type='byte[]'">
        tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="my-ext:upperFirst (@name)"/>: ");
        tSB.append( MessageAux.format( m<xsl:value-of select="my-ext:upperFirst (@name)"/>));
        tSB.append("\n");</xsl:if>
      <xsl:if test="not(@type='byte[]')">
       tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="my-ext:upperFirst (@name)"/>: ");
       tSB.append( String.valueOf( m<xsl:value-of select="my-ext:upperFirst (@name)"/> ));
       tSB.append("\n"); </xsl:if></xsl:if>
</xsl:template>

<xsl:template mode="formatMessage" match="Attribute">
   <xsl:if test="@array">
       tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select='my-ext:upperFirst (@name)'/>[]: ");
         if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
       tSB.append("&lt;null&gt;");
     } else {
     	    tSB.append("\n");
          int tSize = m<xsl:value-of select="my-ext:upperFirst (@name)"/>.size();
          for( int i = 0; i &lt; tSize; i++ ) {
             <xsl:value-of select="@type"/> tMsg = (<xsl:value-of select="@type"/>) m<xsl:value-of select="my-ext:upperFirst (@name)"/>.get( i );
            tSB.append(  tMsg.toString( pCount + 4 ) );
          }
     } </xsl:if>
     <xsl:if test="not(@array)">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="my-ext:upperFirst (@name)"/>: ");
     if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
       tSB.append("&lt;null&gt;");
     } else {
          tSB.append( m<xsl:value-of select="my-ext:upperFirst (@name)"/>.toString( pCount + 4 ) );
     } </xsl:if>
     tSB.append("\n");
</xsl:template>

<xsl:template mode="formatConstant" match="Attribute">
       tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select='my-ext:upperFirst (@name)'/>: ");
      if ( m<xsl:value-of select="my-ext:upperFirst (@name)"/> == null) {
        tSB.append("&lt;null&gt;");
     } else {
          tSB.append(  m<xsl:value-of select="my-ext:upperFirst (@name)"/>.toString() );
     } 
     tSB.append("\n");
</xsl:template>

<xsl:template mode="declareFormater" match="Message">

String blanks(int pCount) {
	if (pCount == 0) {
	  return null;
	}
	String tBlanks = "                                                                                       ";
	return tBlanks.substring( 0,pCount ); 
}


public String toString() {
    return this.toString(0);
 }

public String toString( int pCount ) {
	return toString( pCount, false );
}

public String toString( int pCount, boolean pExtention ) {
    StringBuilder tSB = new StringBuilder (512);
    if (pCount > 0) {
      tSB.append( blanks( pCount ));
    }
    
    if (pExtention)  {
    	tSB.append("&lt;Extending Message: " + "\"<xsl:value-of select="@name"/>\"  Id: " + Integer.toHexString(getMessageId()) + "&gt;\n");
    } else {
    		tSB.append("Message: " + "\"<xsl:value-of select="@name"/>\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	<xsl:if test="@extends">tSB.append( super.toString( pCount + 3, true ));</xsl:if>

      <xsl:for-each select="Attribute">
      		<xsl:if test="@constantGroup">
      			<xsl:apply-templates mode="formatConstant" select="."/>
      		</xsl:if>
      	<xsl:if test="not(@constantGroup)">
        <xsl:variable name="dataType" select="@type"/>
        <xsl:if test="$typeTable/Type[@name=$dataType]">
          <xsl:apply-templates mode="formatNative" select="."/>
        </xsl:if>
        <xsl:if test="not($typeTable/Type[@name=$dataType])">
           <xsl:apply-templates mode="formatMessage" select="."/>
        </xsl:if>
        </xsl:if>
	</xsl:for-each>
	return tSB.toString();
  }
</xsl:template>









<!-- ============================================================================ -->
<!--							Declare Clone Method										-->
<!-- ============================================================================ -->

<xsl:template mode="declareCloneMethod" match="Message">

public <xsl:value-of select="@name"/> clone() {
	<xsl:value-of select="@name"/> tMessage = new <xsl:value-of select="@name"/>();
	<xsl:if test="@extends">
		((<xsl:value-of select="@extends"/>) tMessage).clone();
	</xsl:if>
	<xsl:for-each select="Attribute">
	      <xsl:variable name="dataType" select="@type"/>
     	     <xsl:if test="@constantGroup">
     	     if (this.m<xsl:value-of select="my-ext:upperFirst (@name)"/> != null) { 
     	    	 tMessage.m<xsl:value-of select="my-ext:upperFirst (@name)"/> = <xsl:value-of select="$constantPrefix"/>.<xsl:value-of select="@constantGroup"/>.valueOf( this.m<xsl:value-of select="my-ext:upperFirst (@name)"/>.toString() );
     	    }</xsl:if>		
     	    <xsl:if test="not(@constantGroup)">
     	     <xsl:if test="$typeTable/Type[@name=$dataType]">
     	     tMessage.m<xsl:value-of select="my-ext:upperFirst (@name)"/> = MessageAux.copyAttribute( this.m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:if>
	     <xsl:if test="not($typeTable/Type[@name=$dataType]) and not(@array)">
     	     tMessage.m<xsl:value-of select="my-ext:upperFirst (@name)"/> = (<xsl:value-of select="$dataType"/>) MessageAux.copyAttribute( this.m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:if>
	    <xsl:if test="not($typeTable/Type[@name=$dataType]) and @array">
     	     tMessage.m<xsl:value-of select="my-ext:upperFirst (@name)"/> = (ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;) MessageAux.copyAttribute( this.m<xsl:value-of select="my-ext:upperFirst (@name)"/> );</xsl:if>	    
	    </xsl:if>		
	</xsl:for-each>
	return tMessage;
}  
</xsl:template>


<!--     ============================================== -->
<!--     			      Generate Message Read Only Interface	  -->
<!--     ============================================== -->
<xsl:template mode="generateMessageReadOnlyInterface" match="Message">
<xsl:variable name="constPrefix" select="../@prefix"/>
<redirect:write select="concat($outSource,@name,'ReadOnlyInterface.java')">
package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.ev112.codeblack.common.messaging.MessageJsonDecoder;
import com.ev112.codeblack.common.messaging.MessageJsonEncoder;
import com.ev112.codeblack.common.messaging.MessageBinDecoder;
import com.ev112.codeblack.common.messaging.MessageBinEncoder;
import com.ev112.codeblack.common.messaging.MessageInterface;
import com.ev112.codeblack.common.messaging.MessageServiceInterface;
import com.ev112.codeblack.common.messaging.MessageAux;
import com.ev112.codeblack.common.messaging.MessageWrapper;




<xsl:apply-templates mode="addImports" select="../Imports"/>
<xsl:apply-templates mode="addImports" select="./Imports"/>

public interface <xsl:value-of select="@name"/>ReadOnlyInterface <xsl:if test="@service">extends  MessageServiceInterface</xsl:if>
{
	<xsl:for-each select="Attribute">
	      <xsl:variable name="dataType" select="@type"/>
	      
	<xsl:if test="not(@array) and (($typeTable/Type[@name=$dataType]) or (@constantGroup))">
	public <xsl:value-of select="$dataType"/> get<xsl:value-of select="my-ext:upperFirst (@name)"/>();</xsl:if>
	
	<xsl:if test="not(@array) and not($typeTable/Type[@name=$dataType]) and not(@constantGroup)">
	public <xsl:value-of select="$dataType"/>ReadOnlyInterface get<xsl:value-of select="my-ext:upperFirst (@name)"/>();</xsl:if>
	      	
	<xsl:if test="($typeTable/Type[@name=$dataType]) and @array">      	
	public <xsl:value-of select="$dataType"/>[] get<xsl:value-of select="my-ext:upperFirst (@name)"/>();</xsl:if>	       
	      	
	<xsl:if test="not($typeTable/Type[@name=$dataType]) and @array">
	public List&lt;<xsl:value-of select="$dataType"/>ReadOnlyInterface &gt; get<xsl:value-of select="my-ext:upperFirst (@name)"/>();</xsl:if>
	</xsl:for-each>

	public <xsl:value-of select="@name"/> clone();
	public String getMessageName();
	public String getFullMessageName();
 	public int getMessageId();
 	
}

</redirect:write>
</xsl:template>

<!--     ============================================== -->
<!--     			      Generate DB annotation tags			 	  -->
<!--     ============================================== -->

<xsl:template mode="dbAnnotationAttribute" match="Attribute">
<xsl:if test="../@dbObject">
<xsl:if test="@dbKey">@Id</xsl:if>
<xsl:if test="@dbTransient">@Transient</xsl:if>
<xsl:if test="not(@dbTransient)">
	<xsl:apply-templates mode="dbAnnotationAttributeAttribute" select="."/>
</xsl:if>
</xsl:if>
</xsl:template>

<xsl:template mode="dbAnnotationAttributeAttribute" match="Attribute">
    <xsl:variable name="dataType" select="@type"/>   
    <xsl:if test="@constantGroup">@Enumerated(EnumType.STRING)</xsl:if>
    <xsl:if test="not(@constantGroup)">
	 <xsl:if test="not($typeTable/Type[@name=$dataType])">
            	<xsl:if test="not(@array)">
@OneToOne(  cascade = CascadeType.ALL )
@IndexColumn(name = "<xsl:value-of select='@name'/>Pos", base=1)
            	</xsl:if>
            	<xsl:if test="@array">
@OneToMany(  cascade = CascadeType.ALL )
@IndexColumn(name = "<xsl:value-of select='@name'/>Pos", base=1)
            	</xsl:if>
        </xsl:if>
   </xsl:if>
   <xsl:if test="@dbCol">
@Column( name="<xsl:value-of select='@dbCol'/>")</xsl:if> 
   <xsl:if test="not(@dbCol)">
	<xsl:if test="../@dbAttrPrefix">
@Column( name="<xsl:value-of select='../@dbAttrPrefix'/><xsl:value-of select='my-ext:upperFirst (@name)'/>")
   	</xsl:if></xsl:if>
</xsl:template>


<xsl:template mode="dbAnnotationClass" match="Message">
<xsl:if test="@dbObject">	
	<xsl:choose>
		<xsl:when test="@dbObject='Entity'">
@Entity</xsl:when>
		<xsl:when test="@dbObject='Embeddable'">
@Embeddable
		</xsl:when>
		<xsl:when test="@dbObject='Inherit'">
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
		</xsl:when>
		<xsl:otherwise>
			ERROR: not a valid dbObject value in XML!!!!
		</xsl:otherwise>
	</xsl:choose>

</xsl:if>
</xsl:template>

</xsl:stylesheet>
