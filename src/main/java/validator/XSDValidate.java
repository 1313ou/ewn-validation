package validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XSDValidate
{
	/**
	 * Make validator
	 *
	 * @param xsd
	 *            xsd file
	 * @return validator
	 * @throws SAXException
	 *             exception
	 */
	public static Validator makeValidator(final String xsd) throws SAXException
	{
		URL xsdUrl;
		try
		{
			xsdUrl = XSDValidate.class.getResource(xsd);
			if (xsdUrl == null)
				throw new RuntimeException("Null XSD resource file");
		}
		catch (final Exception e)
		{
			try
			{
				xsdUrl = new URL(xsd);
			}
			catch (final Exception e1)
			{
				try
				{
					xsdUrl = new File(xsd).toURI().toURL();
				}
				catch (final Exception e2)
				{
					throw new RuntimeException("No validator XSD file");
				}
			}
		}

		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = schemaFactory.newSchema(xsdUrl);
		final Validator validator = schema.newValidator();
		System.out.println("XSD: " + xsdUrl);
		return validator;
	}

	/**
	 * Validate
	 *
	 * @param validator
	 *            validator
	 * @param source
	 *            source to validate
	 * @throws SAXException
	 *             exception
	 * @throws IOException
	 *             exception
	 * @throws TransformerException
	 *             exception
	 */
	public static void validate(final Validator validator, final Source source) throws SAXException, IOException, TransformerException
	{
		validator.setErrorHandler(new ErrorHandler()
		{
			@Override
			public void warning(SAXParseException e) throws SAXException
			{
				System.err.println("warning " + e);
			}

			@Override
			public void error(SAXParseException e) throws SAXException
			{
				System.err.println("error " + e);
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException
			{
				System.err.println("fatal " + e);
				throw e;
			}

		});
		validator.validate(source);
	}

	/**
	 * Validate
	 *
	 * @param validator
	 *            validator
	 * @param file
	 *            file to validate
	 * @throws SAXException
	 *             exception
	 * @throws IOException
	 *             exception
	 * @throws TransformerException
	 *             exception
	 */
	public static void validate(final Validator validator, final String filename) throws SAXException, IOException, TransformerException
	{
		System.out.println("validate " + filename);
		validate(validator, new StreamSource(filename));
	}

	/**
	 * Validate
	 *
	 * @param xsd
	 *            xsd file
	 * @param filename
	 *            file to validate
	 * @throws SAXException
	 *             exception
	 * @throws IOException
	 *             exception
	 * @throws TransformerException
	 *             exception
	 */
	public static void validateOne(final String xsd, final String filename) throws SAXException, IOException, TransformerException
	{
		final Validator validator = makeValidator(xsd);
		validate(validator, filename);
	}

	/**
	 * Validate all
	 *
	 * @param xsd
	 *            xsd path
	 * @param filenames
	 *            files
	 * @throws SAXException
	 *             exception
	 */
	public static void validateAll(final String xsd, final String... filenames) throws SAXException
	{
		final Validator validator = makeValidator(xsd);
		for (final String filename : filenames)
		{
			System.out.println("\n*** validating " + filename);
			try
			{
				validate(validator, filename);
			}
			catch (final SAXException e)
			{
				System.out.println("->fail");
				System.err.println(e.getMessage());
			}
			catch (final IOException e)
			{
				System.out.println("->fail");
				System.err.println(e);
			}
			catch (final TransformerException e)
			{
				System.out.println("->fail");
				System.err.println(e);
			}
		}
	}

	/**
	 * Main
	 *
	 * @param args
	 *            args[0] xsd, args[1..] files to validate
	 * @throws SAXException
	 *             exception
	 */
	public static void main(final String[] args)
	{
		try
		{
			validateAll(args[0], Arrays.copyOfRange(args, 1, args.length));
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}