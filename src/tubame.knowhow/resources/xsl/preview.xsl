<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:import href="./docbook-xsl-ns-1.78.1/html/docbook.xsl" />
	<xsl:output method="html" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1"/>
	<xsl:param name="html.stylesheet">css/docbook.css</xsl:param>
	<xsl:param name="section.autolabel" select="1"/>
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1"/>
</xsl:stylesheet>
