<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns="http://www.w3.org/2000/svg" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
<svg viewBox="0 0 1200 800" preserveAspectRatio="none">

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
.U { fill:salmon; }

path {
	stroke:lightgrey; stroke-width:2;
}

g {
	fill:lightgrey;
}

</style>


<g transform="translate(100,100)">
	<xsl:apply-templates select="//square"/>
</g>

</svg>
</xsl:template>

<xsl:template match="square">

	<xsl:variable name="x" select="@col * 50"/>
	<xsl:variable name="y" select="@row * 50"/>
	
	<g transform="translate({$x},{$y})" class="{@pentomino}">
		<xsl:apply-templates select="*"/>
		<circle r="15"/>
	</g>
</xsl:template>

<xsl:template match="NORTH">
	<path d="M0,0 v-35"/>
</xsl:template>

<xsl:template match="SOUTH">
	<path d="M0,0 v35"/>
</xsl:template>

<xsl:template match="EAST">
	<path d="M0,0 h35"/>
</xsl:template>

<xsl:template match="WEST">
	<path d="M0,0 h-35"/>
</xsl:template>

</xsl:stylesheet>
