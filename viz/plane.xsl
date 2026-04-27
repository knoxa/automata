<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/2000/svg" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
<svg viewBox="0 0 3600 2400" preserveAspectRatio="xMinYMin meet">

<style type="text/css">

.P { fill:pink; }
.T { fill:turquoise; }
.Y { fill:gold; }
.F { fill:tomato; }
.I { fill:indigo; }
.V { fill:violet; }
.N { fill:navy; }
.W { fill:steelblue; }
.L { fill:limegreen; }
.X { fill:red; }
.U { fill:teal; }
.Z { fill:orange; }

path {
	stroke:lightgrey; stroke-width:8;
}

g {
	fill:lightgrey;
}

</style>

<xsl:apply-templates select="//equivalent[1]">
	<xsl:with-param name="xoffset" select="0"/>
</xsl:apply-templates>

</svg>
</xsl:template>

<xsl:template match="equivalent">
<xsl:param name="xoffset"/>
<g transform="translate({$xoffset}, 0)">
	<xsl:apply-templates select="plane"/>
</g>
<xsl:apply-templates select="following-sibling::equivalent[1]">
	<xsl:with-param name="xoffset" select="$xoffset + plane[1]/@width * 50 + 80"/>
</xsl:apply-templates>
</xsl:template>

<xsl:template match="plane">

<g transform="translate(100, {@height * 50 * position() + (position()-1)*50})">
	<xsl:apply-templates select="square"/>
</g>

</xsl:template>


<xsl:template match="square">

	<xsl:variable name="x" select="@col * 50"/>
	<xsl:variable name="y" select="@row * 50"/>
	
	<g transform="translate({$x},{$y})" class="{@pentomino}">
		<xsl:apply-templates select="*"/>
		<rect x="-20" y="-20" width="40" height="40"/>
	</g>
</xsl:template>

<xsl:template match="NORTH">
	<path d="M-5,0 v-30"/>
</xsl:template>

<xsl:template match="SOUTH">
	<path d="M5,0 v30"/>
</xsl:template>

<xsl:template match="EAST">
	<path d="M0,-5 h30"/>
</xsl:template>

<xsl:template match="WEST">
	<path d="M0,5 h-30"/>
</xsl:template>

</xsl:stylesheet>
