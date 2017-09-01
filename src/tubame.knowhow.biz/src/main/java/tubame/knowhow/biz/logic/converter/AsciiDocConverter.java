package tubame.knowhow.biz.logic.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.stream.StreamSource;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

import net.sf.saxon.s9api.MessageListener;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;


public class AsciiDocConverter {

	private AsciiDocConverter() {
		super();
	}
	
	public static void convert(String orgFilePath, String outputFilePath,InputStream xslInputStream) throws Exception  {
		Source xmlSource = new StreamSource(orgFilePath);
		Source xsltSource = new StreamSource(xslInputStream);
		File resultFile = new File(outputFilePath);

		Processor proc = new Processor(false);
		XsltCompiler comp = proc.newXsltCompiler();
		XsltExecutable exp = comp.compile(xsltSource);
		XdmNode source = proc.newDocumentBuilder().build(xmlSource);
		Serializer out = new Serializer();
		out.setOutputProperty(Serializer.Property.METHOD, "xml");
		out.setOutputProperty(Serializer.Property.INDENT, "yes");
		out.setOutputFile(resultFile);
		XsltTransformer trans = exp.load();
		trans.setMessageListener(new MessageListener() {
			@Override
			public void message(XdmNode content, boolean terminate, SourceLocator arg2) {
				if (terminate) {
					String xslErrorMessage = content.getStringValue();
					throw new IllegalArgumentException(xslErrorMessage);
				}
			}
		});
		trans.setInitialContextNode(source);
		trans.setDestination(out);
		trans.transform();
	}

	
	public static void toDocBook(String asciidocPath,String outputPath) {
		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(AsciiDocConverter.class.getClassLoader());
		
		JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/2.0", "gems/asciidoctor-1.5.4/lib"), config);
		
		Asciidoctor asciidoctor = Asciidoctor.Factory.create(AsciiDocConverter.class.getClassLoader());
		OptionsBuilder options = OptionsBuilder.options();
		options.backend("docbook").docType("article");
		File adocFile = new File(asciidocPath);
		asciidoctor.convertFile(adocFile,options);
	}
	
	
}
