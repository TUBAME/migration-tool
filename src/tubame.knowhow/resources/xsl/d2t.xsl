<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ns2="http://docbook.org/ns/docbook" xmlns="http://generated.model.biz.knowhow.tubame/knowhow"
	xmlns:xlink="http://www.w3.org/1999/xlink" version="1.0">


	<xsl:include href="./docbook-xsl-ns-1.78.1/html/docbook.xsl" />

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="admon.graphics" select="1" />
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="tablecolumns.extension" select="0" />
	<xsl:param name="use.extensions" select="1" />

	<xsl:param name="categoryIndexBase" select="0" />
	<xsl:param name="knowhowIndexBase" select="0" />
	<xsl:param name="knowhowDetailsIndexBase" select="0" />

	
	
	<xsl:template match="/">

		<PortabilityKnowhow>
			<PortabilityKnowhowTitle>
				<xsl:value-of select="/ns2:book/ns2:title" />
			</PortabilityKnowhowTitle>

			<xsl:apply-templates select="/ns2:book/ns2:chapter[1]"
				mode="entryView" />

			<ChapterList />

			<xsl:apply-templates select="/ns2:book/ns2:chapter[1]"
				mode="categoryList" />

			<xsl:apply-templates select="/ns2:book/ns2:chapter[1]"
				mode="knowhowList" />

			<xsl:apply-templates select="/ns2:book/ns2:chapter[1]"
				mode="docbookList" />

			<SearchInfomationList />
		</PortabilityKnowhow>
	</xsl:template>


	<xsl:template match="ns2:chapter" mode="entryView">
		<!-- categoriesBodyに、CategoryListタグ配下の情報を保持する -->
		<EntryViewList>
			<xsl:for-each select="//ns2:section">
				<xsl:variable name="cateNum" select="concat('category_',$categoryIndexBase + position())"></xsl:variable>
				<xsl:variable name="cateTitle" select="ns2:title"></xsl:variable>

				<xsl:choose>

					<xsl:when test="count(ancestor::ns2:section) = 0">
						<EntryCategory>
							<EntryCategoryRefKey>
								<xsl:value-of select="$cateNum" />
							</EntryCategoryRefKey>
							<xsl:apply-templates select="ns2:section"
								mode="childEntry">
								<xsl:with-param name="cateNumRef" select="$categoryIndexBase + position() + 1" />
							</xsl:apply-templates>
						</EntryCategory>
					</xsl:when>
					<!-- <xsl:otherwise> <EntryCategory> <EntryCategoryRefKey> <xsl:value-of 
						select="$cateNum" /> </EntryCategoryRefKey> </EntryCategory> </xsl:otherwise> -->
				</xsl:choose>
			</xsl:for-each>
		</EntryViewList>
	</xsl:template>


	<xsl:template match="ns2:chapter" mode="categoryList">
		<CategoryList>
			<xsl:for-each select="//ns2:section">
				<xsl:variable name="cateNum" select="concat('category_', $categoryIndexBase + position())"></xsl:variable>
				<xsl:variable name="cateTitle" select="ns2:title"></xsl:variable>
				<xsl:variable name="knowhowNum" select="concat('knowhow_', $knowhowIndexBase +position())"></xsl:variable>
				<Category categoryId="{$cateNum}" appropriate="true">
					<CategoryName>
						<xsl:value-of select="$cateTitle" />
					</CategoryName>
					<xsl:choose>
						<xsl:when test="ns2:para">
							<KnowhowRefKey>
								<xsl:value-of select="$knowhowNum" />
							</KnowhowRefKey>
						</xsl:when>
					</xsl:choose>
				</Category>
			</xsl:for-each>
		</CategoryList>
	</xsl:template>

	<xsl:template match="ns2:chapter" mode="knowhowList">
		<KnowhowList>
			<xsl:for-each select="//ns2:section">
				<xsl:variable name="knowhowNum" select="concat('knowhow_', $knowhowIndexBase + position())"></xsl:variable>
				<xsl:variable name="knowhowDetailNum"
					select="concat('knowhowDetail_', $knowhowDetailsIndexBase + position())"></xsl:variable>

				<xsl:choose>
					<xsl:when test="ns2:para">
						<KnowhowInfomation knowhowId="{$knowhowNum}"
							knowhowDetailRefKey="{$knowhowDetailNum}">
							<KnowhowNo>
								<xsl:value-of select="$knowhowDetailsIndexBase + position()" />
							</KnowhowNo>
							<KnowhowName>body</KnowhowName>
						</KnowhowInfomation>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</KnowhowList>
	</xsl:template>


	<xsl:template match="ns2:chapter" mode="docbookList">
		<DocBookList>
			<xsl:for-each select="//ns2:section">
				<xsl:variable name="knowhowDetailNum"
					select="concat('knowhowDetail_', $knowhowDetailsIndexBase + position() )"></xsl:variable>
				<xsl:choose>
					<xsl:when test="ns2:para">
						<DocBook articleId="{$knowhowDetailNum}">
							<ns2:article>
								<ns2:info>
									<xsl:copy-of select="ns2:title" />
								</ns2:info>
								<ns2:section>
									<xsl:copy-of select="ns2:para/child::*" />
								</ns2:section>

							</ns2:article>
						</DocBook>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</DocBookList>

	</xsl:template>

	<xsl:template match="ns2:section" mode="childEntry">
		<xsl:param name="cateNumRef" />

		<ChildEntry>
			<EntryCategoryRefKey>
				<xsl:value-of select="concat('category_', $cateNumRef)" />
			</EntryCategoryRefKey>
		</ChildEntry>
		<xsl:apply-templates select="ns2:section"
								mode="childEntry">
								<xsl:with-param name="cateNumRef" select="$cateNumRef + 1" />
							</xsl:apply-templates>
	</xsl:template>

</xsl:stylesheet>
