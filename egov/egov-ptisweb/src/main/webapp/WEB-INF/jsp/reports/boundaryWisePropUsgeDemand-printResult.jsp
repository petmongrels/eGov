#-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------
<%@ include file="/includes/taglibs.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title><s:text name="zoneWiseDmdRes" /></title>
	<script language="JavaScript" type="text/JavaScript">
		function printwindow()
		{
		  window.print();
		  window.close();  
		}
		</script>
	</head>

<body onload="printwindow();">
<form method="post">
<div class="formmainbox"><div class="formheading"></div>
<div class="headingbg"><s:if test="%{bndryParamStr == null}">
	<s:text name="zoneWisePropResults"/>
</s:if>
<s:else>
	<s:text name="wardWisePropResults"/>
</s:else>
</div>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tablebottom">
  
      <c:choose>
      <c:when test = "${fn:length(zoneMap)<1}">
       <tr>
      <th colspan="6">
        <div class="headingsmallbgnew" align="center"><font color="red"><b><s:text name="noData"/></b></font></div> 
		    </th>
    </tr>
     </c:when>
     <c:otherwise>
    
   <tr>
          <th rowspan="2" class="bluebgheadtd">
			<s:if test="bndryParamStr == null">
				<s:text name="Zone" />
			</s:if>
			<s:else>
				<s:text name="Ward" />
			</s:else>
			</th>
			<th colspan="4" class="bluebgheadtd">
				<s:text name="nonResidential" />
			</th>	
			<th colspan="4" class="bluebgheadtd">
				<s:text name="openPlot" />
			</th>	
			<th colspan="4" class="bluebgheadtd">
				<s:text name="stateGovt" />
			</th>	
			<th colspan="4" class="bluebgheadtd">
				<s:text name="centralGovt" />
			</th>					
			<th colspan="4" class="bluebgheadtd">
				<s:text name="residential" />
			</th>
			<th colspan="4" class="bluebgheadtd">
				<s:text name="mixed" />
			</th>
			<th colspan="4" class="bluebgheadtd">
				<s:text name="aggTotal" />
			</th>
        </tr>
         <tr>
         <s:iterator status="colLables" value="{1, 2, 3, 4, 5, 6, 7}">
	          <th width="6%" class="bluebgheadtd"><s:text name="noOfProps"/></th>
	          <th width="5%" class="bluebgheadtd"><s:text name="arrearDemand"/> </th>
	          <th width="4%" class="bluebgheadtd"><s:text name="currDemand"/></th>
	          <th width="4%" class="bluebgheadtd"><s:text name="totalDemand"/></th>
	          <th width="4%" class="bluebgheadtd"><s:text name="avAmount"/></th>
          </s:iterator>
 		 </tr>
    <s:iterator value="zoneMap" status="bndMap" >
    <tr>
	   	<td width="6%" class="blueborderfortd2">	
		    <s:if test="%{key == '5000'}">
		    <s:text name="totalRs"/>
		    </s:if>
		    <s:else >
		    <s:property value="%{bndryPropUsgeDelgte.getBndryNameById(key)}"/>
			</s:else>
        </td>
        
        <s:iterator value="value" status="propMap">
        <s:if test="%{bndMap.key == '5000'}">
                     
          <td width="6%" class="Total">	
        	 <s:property value="value.propCount"></s:property>        
          </td>
         
          <td width="5%" class="Total">	
       	  	<s:property value="value.arrDmd"/>    
          </td>
          <td width="4%"class="Total">	
        	<s:property value="value.currDmd"/>     
          </td>
          <td width="4%" class="Total">	
        	<s:property value="value.totalDemand"/>   
          </td>
        </s:if>
	    <s:else >
	    
	    <td width="6%" class="blueborderfortd2">	
         <s:property value="value.propCount"></s:property> 
        </td>
         
        <td width="5%" class="blueborderfortd2">	
        	<s:property value="value.arrDmd"/>    
        </td>
        <td width="4%"class="blueborderfortd2">	
        	<s:property value="value.currDmd"/>     
        </td>
        <td width="4%" class="blueborderfortd2">	
        	<s:property value="value.totalDemand"/>   
        </td>
	    </s:else>
            
        </s:iterator>
       
    </tr>
    </s:iterator>
  </c:otherwise>
  </c:choose>
    </table>
	
</div>   
</form>
</body>
</html>
