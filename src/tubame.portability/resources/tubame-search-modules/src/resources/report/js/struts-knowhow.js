jQuery(document).ready(function() {
		//
	   try{
						var fromApresult = $.fn.FrameworkKnowhowDegreeOfDifficultySumCalclator_fromStrutsFw_getResult();
			$("#fromFw_High").text(fromApresult.High);
			$("#fromFw_Middle").text(fromApresult.Middle);
			$("#fromFw_Low2").text(fromApresult.Low2);
			$("#fromFw_Low1").text(fromApresult.Low1);
			$("#fromFw_Unknown").text(fromApresult.Unknown1 + fromApresult.Unknown2);
			$("#fromFw_Total").text(fromApresult.High + fromApresult.Middle
							+ fromApresult.Low2 + fromApresult.Low1
							+ fromApresult.Unknown1
							+ fromApresult.Unknown2);
							
			var fromApresult = $.fn.KnowhowDegreeOfDifficultyHitCountSumCalclator_fromStrutsFw_getResult();
			$("#fromFw_High").append('('+fromApresult.High +')');
			$("#fromFw_Middle").append('('+fromApresult.Middle+')');
			$("#fromFw_Low2").append('('+fromApresult.Low2+')');
			$("#fromFw_Low1").append('('+fromApresult.Low1+')');
			var num = parseInt(fromApresult.Unknown1) + parseInt(fromApresult.Unknown2);
			$("#fromFw_Unknown").append('('+num+')');
			num = fromApresult.High + fromApresult.Middle
							+ fromApresult.Low2 + fromApresult.Low1
							+ fromApresult.Unknown1
							+ fromApresult.Unknown2
			$("#fromFw_Total").append('('+num+')');
							
							
			//
			var toApresult = $.fn.FrameworkKnowhowDegreeOfDifficultySumCalclator_toFw_getResult();
			$("#toFw_High").text(toApresult.High);
			$("#toFw_Middle").text(toApresult.Middle);
			$("#toFw_Low2").text(toApresult.Low2);
			$("#toFw_Low1").text(toApresult.Low1);
			$("#toFw_Unknown").text(toApresult.Unknown1 + toApresult.Unknown2)
			$("#toFw_Total").text(
					toApresult.High + toApresult.Middle
							+ toApresult.Low2 + toApresult.Low1
							+ toApresult.Unknown1
							+ toApresult.Unknown2);
							
			var toApresult = $.fn.KnowhowDegreeOfDifficultyHitCountSumCalclator_toFw_getResult();
			$("#toFw_High").append('('+toApresult.High+')');
			$("#toFw_Middle").append('('+toApresult.Middle+')');
			$("#toFw_Low2").append('('+toApresult.Low2+')');
			$("#toFw_Low1").append('('+toApresult.Low1+')');
			num = parseInt(toApresult.Unknown1) + parseInt(toApresult.Unknown2);
			$("#toFw_Unknown").append('('+num+')');
			num = toApresult.High + toApresult.Middle
							+ toApresult.Low2 + toApresult.Low1
							+ toApresult.Unknown1
							+ toApresult.Unknown2
			$("#toFw_Total").append('('+num+')');
			
			//
			var factorToApresult = $.fn.FrameworkKnowhowFactorSumCalclator_toFw_getResult();
			$("#toFw_factor1").text(factorToApresult.mvcFrameworkM);
			$("#toFw_factor2").text(factorToApresult.mvcFrameworkV);
			$("#toFw_factor3").text(factorToApresult.mvcFrameworkC );
			$("#toFw_factor4").text(factorToApresult.mvcFrameworkSpecificNonBackwardCompati);
			$("#toFw_factor5").text(factorToApresult.mvcFrameworkSpecificBackwardCompati);
			
			
			var factorToApresult = $.fn.FrameworkKnowhowFactorHitCountSumCalclator_toFw_getResult();
			$("#toFw_factor1").append('('+factorToApresult.MVCFramework_Model+')');
			$("#toFw_factor2").append('('+factorToApresult.MVCFramework_View+')');
			$("#toFw_factor3").append('('+factorToApresult.MVCFramework_Controller+')');
			$("#toFw_factor4").append('('+factorToApresult.MVCFrameworkSpecific_NonBackwardCompati+')');
			$("#toFw_factor5").append('('+factorToApresult.MVCFrameworkSpecific_BackwardCompati+')');
			//
			var fileFromApResult = $.fn.FrameworkKnowhowFileCategorySumCalclator_fromStrutsFw_getResult();
			$("#fromFw_Java").text(fileFromApResult.java);
			$("#fromFw_Jsp").text(fileFromApResult.jsp);
			$("#fromFw_Xml").text(fileFromApResult.xml);
			$("#fromFw_Properties").text(fileFromApResult.properties);
			
			//
			var fileToApResult = $.fn.FrameworkKnowhowFileCategorySumCalclator_toFw_getResult();
			$("#toFw_Java").text(fileToApResult.java);
			$("#toFw_Jsp").text(fileToApResult.jsp);
			$("#toFw_Xml").text(fileToApResult.xml);
			$("#toFw_Properties").text(fileToApResult.properties);
			
			var migrationItems = $.fn.KnowhowMigrationItemCalclator_getResult();
			for (var i = 0; i < migrationItems.length; i ++) {
				  var migrationItem = migrationItems[i];
				  $("#migrationItemList").append("<tr><td>"+migrationItem.major+"</td><td>"+migrationItem.minor+"</td><td>"+migrationItem.degreeDetail+"</td><td>"+migrationItem.portabilityFactor+"</td><td>"+migrationItem.guideRef+"</td><td>"+migrationItem.hitCount + "</td><td>"+migrationItem.lineBasic + "</td><td>"+migrationItem.stepCount + "</td><td>"+migrationItem.manualCheckStatus +"</td></tr>");
			}
	   }catch(e){
		   
	   }

});

(function bars_stacked(container, horizontal) {
	try{
		var listOjb = $.fn.StrutsFrameworkKnowhowFactorGraphSumCalclator_graph_getResult();
		var graph = Flotr.draw(container, listOjb, {
			legend : {
				backgroundOpacity : 0,
				sorted : true
			},
			bars : {
				show : true,
				stacked : true,
				horizontal : horizontal,
				barWidth : 0.65,
				lineWidth : 1,
				shadowSize : 0
			},
			grid : {
				verticalLines : horizontal,
				horizontalLines : !horizontal
			},
			xaxis : {
				ticks : [ [ 0, "Struts2へのバージョンアップ" ], [ 1, "SpringMVCへの移植" ]]
			},
		});
	}catch(e){
		
	}
	
})(document.getElementById("graph"));