$(document).ready(function() {
	var dependsErrResult = $.fn.DependsErrSumCalclator_getResult();
	$("#Depends_Err_Java").text(dependsErrResult.Java);
	$("#Depends_Err_Jsp").text(dependsErrResult.Jsp);
	$("#Depends_Err_Xml").text(dependsErrResult.Xml);
	
	var dependsPackagesResults  = $.fn.DependsPackageSumCalclator_getResult();
	for (var i = 0; i < dependsPackagesResults.length; i ++) {
		  var result = dependsPackagesResults[i];
		  console.log(result);
		  $("#dependsPackgeSum").append("<tr><td>"+result.label+"</td><td>"+result.data+"</td>�@+ <td>"+result.classNameResult+"</td>�@</tr>");
	}
	var dependsTldResults  = $.fn.DependsJspUsedTldFileSumCalclator_getResult();
	for (var i = 0; i < dependsTldResults.length; i ++) {
		  var result = dependsTldResults[i];
		  console.log(result);
		  $("#dependsTldSum").append("<tr><td>"+result.label+"</td><td>"+result.data+"</td>�@+ <td></td>�@</tr>");
	}
	
	var dependsXmlSchemaResults  = $.fn.DependsXmlUsedSchemaFileSumCalclator_getResult();
	for (var i = 0; i < dependsXmlSchemaResults.length; i ++) {
		  var result = dependsXmlSchemaResults[i];
		  console.log(result);
		  $("#dependsXmlSchemaSum").append("<tr><td>"+result.label+"</td><td>"+result.data+"</td>�@+ <td></td>�@</tr>");
	}
	
});

(function basic_pie(container) {
	var listOjb = $.fn.DependsPackagePicGrapthSumCalclator_getResult();
	var graph = Flotr.draw(container, listOjb, {
		HtmlText : false,
		grid : {
	      verticalLines : false,
	      horizontalLines : false
	    },
	    xaxis : { showLabels : false },
	    yaxis : { showLabels : false },
	    pie : {
	      show : true, 
	      explode : 6
	    },
	    mouse : { track : true },
	    legend : {
	      position : 'se',
	  backgroundColor : '#D2E8FF'
	    }
	  });
})(document.getElementById("pie-graph"));