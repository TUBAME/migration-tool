<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://docbook.org/ns/docbook" xmlns:knowhow="http://generated.model.biz.knowhow.tubame/knowhow"
	xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="knowhow doc"
	version="1.0">


	<xsl:include href="./docbook-xsl-ns-1.78.1/html/docbook.xsl" />

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1" />
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1" />

	<xsl:template match="/">
		<book xmlns="http://docbook.org/ns/docbook">
			<title>
				<xsl:value-of
					select="/knowhow:PortabilityKnowhow/knowhow:PortabilityKnowhowTitle" />
			</title>
			<!-- <xsl:apply-templates select="/knowhow:PortabilityKnowhow/knowhow:ChapterList/knowhow:Chapter"/> -->

			<xsl:apply-templates
				select="/knowhow:PortabilityKnowhow/knowhow:ChapterList" />

		</book>

	</xsl:template>

	<xsl:template match="knowhow:ChapterList">
		<xsl:apply-templates select="knowhow:Chapter" />
	</xsl:template>

	<xsl:template match="knowhow:Chapter">

		<chapter>
			<title>
				<xsl:value-of select="knowhow:ChapterName" />
			</title>
			<xsl:apply-templates select="knowhow:ChildChapter" />
		</chapter>

		<!-- <xsl:apply-templates select="knowhow:ChildChapter" /> -->
	</xsl:template>


	<xsl:template match="knowhow:ChildChapter">

		<section>
			<title>
				<xsl:call-template name="displayCategoryName">
					<xsl:with-param name="refkey">
						<xsl:value-of select="knowhow:ChapterCategoryRefKey" />
					</xsl:with-param>
				</xsl:call-template>
			</title>


			<!-- display docbook under a category -->
			<xsl:call-template name="searchCategory">
				<xsl:with-param name="ChapterCategoryRefKey">
					<xsl:value-of select="knowhow:ChapterCategoryRefKey" />
				</xsl:with-param>
			</xsl:call-template>

			<xsl:apply-templates select="knowhow:ChildChapter" />
		</section>


	</xsl:template>


	<!-- displayCategoryName -->
	<xsl:template name="displayCategoryName">
		<xsl:param name="refkey">
			-
		</xsl:param>
		<xsl:for-each
			select="/knowhow:PortabilityKnowhow/knowhow:CategoryList/knowhow:Category">
			<xsl:choose>
				<xsl:when test="$refkey=@categoryId">
					<xsl:value-of select="knowhow:CategoryName" />
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>


	<!-- find the "Category" ID matches -->
	<xsl:template name="searchCategory">
		<xsl:param name="ChapterCategoryRefKey">
			-
		</xsl:param>
		<xsl:for-each
			select="/knowhow:PortabilityKnowhow/knowhow:CategoryList/knowhow:Category">
			<xsl:choose>
				<xsl:when test="$ChapterCategoryRefKey=@categoryId">
					<xsl:for-each select="current()/knowhow:KnowhowRefKey">
						<xsl:call-template name="searchKnowhowInformation">
							<xsl:with-param name="KnowhowRefKey">
								<xsl:value-of select="current()" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>


	<!-- find the "Knowhow Information" ID matches -->
	<xsl:template name="searchKnowhowInformation">
		<xsl:param name="KnowhowRefKey">
			-
		</xsl:param>
		<xsl:for-each
			select="/knowhow:PortabilityKnowhow/knowhow:KnowhowList/knowhow:KnowhowInfomation">
			<xsl:choose>
				<xsl:when test="$KnowhowRefKey=@knowhowId">
					<xsl:call-template name="searchDocBook">
						<xsl:with-param name="knowhowDetailRefKey">
							<xsl:value-of select="@knowhowDetailRefKey" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- find the "DocBook" ID matches -->
	<xsl:template name="searchDocBook">
		<xsl:param name="knowhowDetailRefKey">
			-
		</xsl:param>
		<xsl:for-each
			select="/knowhow:PortabilityKnowhow/knowhow:DocBookList/knowhow:DocBook">
			<xsl:choose>
				<xsl:when test="$knowhowDetailRefKey=@articleId">
					<xsl:apply-templates select="doc:article" />
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="doc:article">

		<!-- <xsl:copy-of select="."/> -->
		<xsl:apply-templates mode="copy-no-namespaces"
			select="./doc:section/node()" />

	</xsl:template>

	<!-- https://stackoverflow.com/questions/19998180/xsl-copy-nodes-without-xmlns -->

	<xsl:template match="*" mode="copy-no-namespaces">
		<xsl:element name="{local-name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="node()" mode="copy-no-namespaces" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="comment()| processing-instruction()"
		mode="copy-no-namespaces">
		<xsl:copy />
	</xsl:template>

</xsl:stylesheet>
