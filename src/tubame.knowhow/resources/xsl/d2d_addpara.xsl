<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://docbook.org/ns/docbook" version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1" />
	<xsl:param name="html.stylesheet">
		css/knowhow.css
	</xsl:param>
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1" />


	<xsl:template
		match="doc:section/*[not(self::doc:title) and not(self::doc:section) ]">

		<xsl:if test="name(preceding-sibling::*[position() = 1]) = 'title'">
			<doc:para>
				<xsl:apply-templates
					select=".|following-sibling::*[not(self::doc:section)]" mode="keep" />
			</doc:para>
		</xsl:if>

	</xsl:template>



	<xsl:template match="*" mode="keep">
		<xsl:call-template name="ident" />
	</xsl:template>

	<xsl:template match="@*|node()" name="ident">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="doc:simpara">
		<xsl:element name="doc:para">
			<xsl:apply-templates select="node()|@*" />
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>