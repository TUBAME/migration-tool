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

	<xsl:template match="doc:simpara">
			<xsl:element name="doc:para">
				<xsl:apply-templates select="node()|@*" />
			</xsl:element>
	</xsl:template>


	<xsl:template match="doc:link[@xlink:href]">

		<xsl:element name="doc:command">
			<xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
			      <xsl:value-of select="@xlink:href" />
     		 </xsl:attribute>

			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<xsl:template match="doc:link[@linkend]">
		<xsl:variable name="linkend" select="@linkend" />
		<xsl:element name="doc:command">
			<xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
			      <xsl:value-of select="concat('#',$linkend)" />
     		 </xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<xsl:template match="doc:programlisting">
		<xsl:variable name="programlistingValue" select="." />
		<xsl:if test="starts-with($programlistingValue, 'link:')">
			<xsl:variable name="filePath"
				select="substring-before(substring-after($programlistingValue,':'),'[')" />
			<doc:programlisting>
				<doc:textobject>
					<doc:textdata fileref="{$filePath}" />
				</doc:textobject>
			</doc:programlisting>
		</xsl:if>
		<xsl:if test="not(starts-with($programlistingValue, 'link:'))">
			<xsl:copy>
				<xsl:apply-templates select="node()|@*" />
			</xsl:copy>
		</xsl:if>
	</xsl:template>

	<xsl:template match="doc:formalpara">
		<doc:figure>
			<xsl:apply-templates select="node()|@*" />
		</doc:figure>
	</xsl:template>

</xsl:stylesheet>