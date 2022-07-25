<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" omit-xml-declaration="yes" />

    <xsl:template match="/">
        <xsl:apply-templates>
            <xsl:sort select="count(*)" data-type="number" order="ascending" />
            <xsl:sort select="name()" order="ascending" />		
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="node()">
        <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates>
                <xsl:sort select="count(*)" data-type="number" order="ascending" />
                <xsl:sort select="name()" order="ascending" />
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

<!--<xsl:template match="decFormatoFileDocs">
	<xsl:copy>
    <xsl:copy-of select="@*" />
    <xsl:apply-templates select="decUsoFormatoFileStandards">
		<xsl:sort select="niOrdUso" order="ascending" />
    </xsl:apply-templates>
	<xsl:apply-templates />
  </xsl:copy>
</xsl:template>-->

    <xsl:template match="decUsoFormatoFileStandards">
        <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates>
                <xsl:sort select="niOrdUso" order="ascending" />
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>