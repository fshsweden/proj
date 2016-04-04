/*
 * "$Id: Transform.java,v 1.3 2004/02/19 11:15:26 axel Exp $"
 *
 * Copyright (c) 2004 American Stock Exchange LLC. All rights reserved.
 *
 * This software is the confidential and proprietary information of American Stock Exchange LLC.
 */


// Imported TraX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import org.w3c.dom.*;

// Imported java classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Script generation
 *
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (3 statements).
 */
public class Transform
{
	static Hashtable<String,String> cImports = new Hashtable<String,String>();
	static String cXmlBasePath = null;
	static boolean cFormatCode = true;
	static boolean cLogWork = true;
	static boolean cDebug = false;
    static SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");


    public  static void  transformModel( Node rootNode, String xslFile, String outSource  )
    {
        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource( xslFile ));

            transformer.setParameter("inputXsl", xslFile.replace('\\','/'));
             transformer.setParameter("outSource", outSource.replace('\\','/'));
             cFormatCode = false;
             cLogWork = true;
             cDebug = false;

             DOMSource ds = new DOMSource( rootNode );
             transformer.transform( ds, new StreamResult(System.out));
             System.out.println("************* All done *************");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }


	public static void main(String[] args) throws TransformerException, TransformerConfigurationException, FileNotFoundException, IOException
	{
		// Use the static TransformerFactory.newInstance() method to instantiate
		// a TransformerFactory. The javax.xml.transform.TransformerFactory
		// system property setting determines the actual class to instantiate --
		// org.apache.xalan.transformer.TransformerImpl.
		TransformerFactory tFactory = TransformerFactory.newInstance();

		// Use the TransformerFactory to instantiate a Transformer that will work with
		// the stylesheet you specify. This method call also processes the stylesheet
		// into a compiled Templates object.
		Transformer transformer = tFactory.newTransformer(new StreamSource(args[0]));

		// Use the Transformer to apply the associated Templates object to an XML document
		// (foo.xml) and write the output to a file (foo.out).

		if (args.length >=1)
			transformer.setParameter("inputXsl", args[0].replace('\\','/'));

		if (args.length >=2)
			transformer.setParameter("inputXml", args[1].replace('\\','/'));

		if (args.length >= 3)
        {
			transformer.setParameter("outSource", args[2].replace('\\','/'));
        }

		for (int i = 3; i < args.length; ++i)
		{
			String option = args[i].toLowerCase();

			if (option.indexOf("-noformat") != -1)
				cFormatCode = false;

			if (option.indexOf("-nolog") != -1)
				cLogWork = false;

			if (option.indexOf("-debug") != -1)
				cDebug = true;
		}

		File path = new File( args[1] );
		cXmlBasePath = path.getCanonicalFile().getParent();
		transformer.setParameter("inputXmlPath", cXmlBasePath.replace('\\','/'));

		transformer.transform(new StreamSource(args[1]), new StreamResult(System.out));

		if (cLogWork)
			System.out.println("************* All done *************");

	}

	static public String regImport(String attribute, String prefix)
	{
		String s = "";
		if (attribute.length() == 0)
		{
			for (Enumeration<String> e = cImports.elements(); e.hasMoreElements();)
				s += e.nextElement().toString();

			cImports = new Hashtable<String,String>();
			return s;
		}
		else
		{
			cImports.put(attribute, prefix + attribute + ";\n");
		}
		return s;
	}

	static public String lowerFirst(String name)
	{
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	static public String upperFirst(String name)
	{
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	static public String replace( String pString, String pOldPattern, String pNewPattern ) {
		return pString.replace( pOldPattern, pNewPattern );
	}

	static public boolean endsWith( String pString, String pSuffix )
	{
		if (pString == null)
		  return false;

		if (pString.endsWith(pSuffix))
		  return true;
		else
			return false;
	}

	static public boolean startsWith( String pString, String pPrefix )
	{
		if (pString == null)
		  return false;

		if (pString.startsWith(pPrefix))
		  return true;
		else
			return false;
	}


	static public class Redirect extends org.apache.xalan.lib.Redirect
	{

		public void open(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, java.io.FileNotFoundException, java.io.IOException, javax.xml.transform.TransformerException
		{
			String fileName = getFilename(context, elem);
			System.out.println("write filename is " + fileName );
			super.open(context, elem);
		}


		/**
		 * Write the evalutation of the element children to the given file. Then close the file
		 * unless it was opened with the open extension element and is in the formatter listener's table.
		 */
		public void write(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, java.io.FileNotFoundException, java.io.IOException, javax.xml.transform.TransformerException
		{
			String fileName = getFilename(context, elem);
            File file = new File( cXmlBasePath, fileName );
            boolean exists = file.exists();
            try
            {
                if (exists)
                    exists = !file.delete();
            }
            catch (Throwable e)
            {
                System.out.println("Error - problems deleting file" + file.getCanonicalPath());
                System.out.println("Error - " + e.getMessage() );
            }

            if (exists)
            {
                System.out.println("Error - problems deleting file" + file.getCanonicalPath());
            }
			context.getTransformer().setParameter("timeStamp", mSdf.format(new Date()));

			String currentXslFile = context.getStylesheet().getHref().toString();
			if (currentXslFile.startsWith("file:///"))
			{
				currentXslFile = currentXslFile.substring("file:///".length());
			}
			context.getTransformer().setParameter("currentXslFile", currentXslFile.replace('\\','/'));

			if (cDebug)
			{
				System.out.println("[Generating: "+file.getCanonicalPath()+"]");
				System.out.println("[xsl used:   "+currentXslFile.replace('\\','/')+"]");
			}

			try {
				super.write(context, elem);
			}
			catch( Exception e )
			{
				System.out.println("Error - writing to file" + file.getCanonicalPath());
				System.out.println("Error - " + e.getMessage() );
				e.printStackTrace();
				// eat it.
			}



			if (cLogWork)
	            System.out.println("["+file.getCanonicalPath()+"]");
		}

		/**
		 * Get the filename from the 'select' or the 'file' attribute.
		 */
		private String getFilename(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, java.io.FileNotFoundException, java.io.IOException, javax.xml.transform.TransformerException
		{
			String fileName;
			String fileNameExpr = ((ElemExtensionCall) elem).getAttribute("select", context.getContextNode(), context.getTransformer());
			if (null != fileNameExpr)
			{
				org.apache.xpath.XPathContext xctxt = context.getTransformer().getXPathContext();
				XPath myxpath = new XPath(fileNameExpr, elem, xctxt.getNamespaceContext(), XPath.SELECT);
				XObject xobj = myxpath.execute(xctxt, context.getContextNode(), elem);
				fileName = xobj.str();
				if ((null == fileName) || (fileName.length() == 0))
				{
					fileName = elem.getAttribute("file", context.getContextNode(), context.getTransformer());
				}
			}
			else
			{
				fileName = elem.getAttribute("file", context.getContextNode(), context.getTransformer());
			}
			if (null == fileName)
			{
				context.getTransformer().getMsgMgr().error(elem, elem, context.getContextNode(), XSLTErrorResources.ER_REDIRECT_COULDNT_GET_FILENAME);
				//"Redirect extension: Could not get filename - file or select attribute must return vald string.");
			}
			return fileName;
		}
	}


}