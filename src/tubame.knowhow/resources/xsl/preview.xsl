<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://docbook.org/ns/docbook"
	xmlns:knowhow="http://generated.model.biz.knowhow.tubame/knowhow"
	version="1.0">

	<xsl:include href="./docbook-xsl-ns-1.78.1/html/docbook.xsl" />
	<xsl:output method="html" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1" />
	<xsl:param name="html.stylesheet">css/knowhow.css</xsl:param>
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1" />
	
    <xsl:template match="doc:programlisting">
        <iframe src="{./doc:textobject/doc:textdata/@fileref}" />
    </xsl:template>
    
</xsl:stylesheet>
