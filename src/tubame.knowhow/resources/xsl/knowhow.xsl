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

	<xsl:template match="/">
		<HTML>
			<HEAD>
				<xsl:call-template name="displayHeadContent" />
			</HEAD>
			<BODY>
				<H1 class="title">
					<xsl:apply-templates
						select="/knowhow:PortabilityKnowhow/knowhow:PortabilityKnowhowTitle" />
				</H1>
				<xsl:apply-templates
					select="/knowhow:PortabilityKnowhow/knowhow:ChapterList" mode="index" />
				<HR />
				<xsl:apply-templates
					select="/knowhow:PortabilityKnowhow/knowhow:ChapterList" />
			</BODY>
		</HTML>
	</xsl:template>

	<!-- ================ -->
	<!-- Common templates -->
	<!-- ================ -->
	<!-- displayHeadContent -->
	<xsl:template name="displayHeadContent">
		<xsl:param name="title">
			<xsl:apply-templates
				select="/knowhow:PortabilityKnowhow/knowhow:PortabilityKnowhowTitle" />
		</xsl:param>
		<xsl:call-template name="head.content">
			<xsl:with-param name="title" select="$title" />
		</xsl:call-template>
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

	<!-- ==================== -->
	<!-- Individual templates -->
	<!-- ==================== -->
	<!-- title -->
	<xsl:template match="knowhow:PortabilityKnowhowTitle">
		<xsl:apply-templates />
	</xsl:template>

	<!-- index -->
	<xsl:template match="knowhow:ChapterList" mode="index">
		<div class="toc">
			<p>
				<b>Index</b>
			</p>
			<dl class="toc">
				<xsl:apply-templates select="knowhow:Chapter"
					mode="index" />
			</dl>
		</div>
	</xsl:template>
	<xsl:template match="knowhow:Chapter" mode="index">
		<dt>
			<span>
				<xsl:attribute name="class">
					<xsl:value-of select="knowhow:ChapterNo" />
				</xsl:attribute>
				<a href="#tubame_{knowhow:ChapterNo}">
					<xsl:value-of select="knowhow:ChapterNo" />
					<xsl:value-of select="knowhow:ChapterName" />
				</a>
			</span>
		</dt>
		<xsl:apply-templates select="knowhow:ChildChapter"
			mode="index" />
	</xsl:template>
	<xsl:template match="knowhow:ChildChapter" mode="index">
		<dd>
			<dl>
				<dt>
					<span>
						<xsl:attribute name="class">
							<xsl:value-of select="knowhow:ChildCapterNo" />
						</xsl:attribute>
						<a href="#tubame_{knowhow:ChildCapterNo}">
							<xsl:value-of select="knowhow:ChildCapterNo" />
							<xsl:call-template name="displayCategoryName">
								<xsl:with-param name="refkey">
									<xsl:value-of select="knowhow:ChapterCategoryRefKey" />
								</xsl:with-param>
							</xsl:call-template>
						</a>
					</span>
				</dt>
				<xsl:apply-templates select="knowhow:ChildChapter"
					mode="index" />
			</dl>
		</dd>
	</xsl:template>

	<!-- body -->
	<xsl:template match="knowhow:ChapterList">
		<xsl:apply-templates select="knowhow:Chapter" />
	</xsl:template>
	<xsl:template match="knowhow:Chapter">
		<a name="tubame_{knowhow:ChapterNo}"></a>
		<h2 class="title">
			<xsl:value-of select="knowhow:ChapterNo" />
			<xsl:value-of select="knowhow:ChapterName" />
		</h2>
		<br />
		<xsl:apply-templates select="knowhow:ChildChapter" />
	</xsl:template>
	<xsl:template match="knowhow:ChildChapter">
		<a id="{knowhow:ChapterCategoryRefKey}" name="tubame_{knowhow:ChildCapterNo}"></a>
		<h3 class="title">
			<xsl:value-of select="knowhow:ChildCapterNo" />
			<xsl:call-template name="displayCategoryName">
				<xsl:with-param name="refkey">
					<xsl:value-of select="knowhow:ChapterCategoryRefKey" />
				</xsl:with-param>
			</xsl:call-template>
		</h3>
		<br />

		<!-- display docbook under a category -->
		<xsl:call-template name="searchCategory">
			<xsl:with-param name="ChapterCategoryRefKey">
				<xsl:value-of select="knowhow:ChapterCategoryRefKey" />
			</xsl:with-param>
		</xsl:call-template>
		<br />

		<xsl:apply-templates select="knowhow:ChildChapter" />
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

	<!-- Output article data -->
	<xsl:template match="doc:article">
		<xsl:apply-templates />
		<br />
	</xsl:template>

</xsl:stylesheet>
