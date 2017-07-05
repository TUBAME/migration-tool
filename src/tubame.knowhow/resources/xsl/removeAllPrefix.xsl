<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://docbook.org/ns/docbook" xmlns:xlink="http://www.w3.org/1999/xlink">
  <xsl:output method="xml" indent="yes"/>

  
  <!-- remove prefix -->
  <xsl:template match="*">
    <xsl:element name="{local-name(.)}">
      <xsl:apply-templates select="@* | node()"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="@*">
    <xsl:attribute name="{local-name(.)}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <!-- remain xlink prefix --> 
   <xsl:template match="@xlink:href">
      <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
        <xsl:value-of select="."/>
      </xsl:attribute>
   </xsl:template>

  
  <!-- trim para/text for adoc -->
  <xsl:template match="para/text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
</xsl:stylesheet>
