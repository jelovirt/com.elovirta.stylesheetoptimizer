<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">

  <xsl:template name="get-original-element" as="xs:string">
    <xsl:choose>
      <xsl:when test="@class">
        <xsl:value-of select="replace(@class, '^[\+\-]\s+[^/]+?/([^\s]+?)\s+.*$', '$1')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="local-name()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="generalize-domain">
    <xsl:param name="class"/>
    <xsl:param name="domains"/>
    <xsl:value-of select="replace(@class, '^[\+\-]\s+[^/]+?/([^\s]+?)\s+.*$', '$1')"/>
  </xsl:template>

</xsl:stylesheet>
