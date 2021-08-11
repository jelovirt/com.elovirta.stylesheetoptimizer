<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">

  <xsl:template match="/">
    <xsl:variable name="buf" as="node()+">
      <xsl:next-match/>
    </xsl:variable>
    <xsl:apply-templates select="$buf" mode="generalize-name"/>
  </xsl:template>

  <xsl:template match="node() | @*" mode="generalize-name">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*[namespace-uri() = '' and @class]" mode="generalize-name">
    <xsl:variable name="name" as="xs:string">
      <xsl:variable name="base" select="replace(@class, '^[\+\-]\s+[^/]+?/([^\s]+?)\s+.*$', '$1')"
        as="xs:string"/>
      <xsl:choose>
        <xsl:when test="$base = local-name()">
          <xsl:value-of select="local-name()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$base"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$name}">
      <xsl:apply-templates select="node() | @*" mode="#current"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
