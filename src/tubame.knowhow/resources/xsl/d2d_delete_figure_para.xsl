<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://docbook.org/ns/docbook" xmlns:xlink="http://www.w3.org/1999/xlink"
	version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1" />
	<xsl:param name="html.stylesheet">
		css/knowhow.css
	</xsl:param>
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1" />

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="doc:figure/doc:para">
		<xsl:if test="doc:programlisting">
			<xsl:apply-templates select="doc:programlisting" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>