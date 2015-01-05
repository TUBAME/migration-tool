jQuery(document).ready(function() {
		//
	   try{
			var fromApresult = $.fn.KnowhowDegreeOfDifficultySumCalclator_fromAp_getResult();
			$("#fromAp_High").text(fromApresult.High);
			$("#fromAp_Middle").text(fromApresult.Middle);
			$("#fromAp_Low2").text(fromApresult.Low2);
			$("#fromAp_Low1").text(fromApresult.Low1);
			$("#fromAp_Unknown").text(fromApresult.Unknown1 + fromApresult.Unknown2);
			$("#fromAp_Total").text(fromApresult.High + fromApresult.Middle
							+ fromApresult.Low2 + fromApresult.Low1
							+ fromApresult.Unknown1
							+ fromApresult.Unknown2);
							
			var fromApresult = $.fn.KnowhowDegreeOfDifficultyHitCountSumCalclator_fromAp_getResult();
			$("#fromAp_High").append('('+fromApresult.High +')');
			$("#fromAp_Middle").append('('+fromApresult.Middle+')');
			$("#fromAp_Low2").append('('+fromApresult.Low2+')');
			$("#fromAp_Low1").append('('+fromApresult.Low1+')');
			var num = parseInt(fromApresult.Unknown1) + parseInt(fromApresult.Unknown2);
			$("#fromAp_Unknown").append('('+num+')');
			num = fromApresult.High + fromApresult.Middle
							+ fromApresult.Low2 + fromApresult.Low1
							+ fromApresult.Unknown1
							+ fromApresult.Unknown2
			$("#fromAp_Total").append('('+num+')');
			//
			var toApresult = $.fn.KnowhowDegreeOfDifficultySumCalclator_toAp_getResult();
			$("#toAp_High").text(toApresult.High);
			$("#toAp_Middle").text(toApresult.Middle);
			$("#toAp_Low2").text(toApresult.Low2);
			$("#toAp_Low1").text(toApresult.Low1);
			$("#toAp_Unknown").text(
					toApresult.Unknown1 + toApresult.Unknown2)
			$("#toAp_Total").text(
					toApresult.High + toApresult.Middle
							+ toApresult.Low2 + toApresult.Low1
							+ toApresult.Unknown1
							+ toApresult.Unknown2);
			
			var toApresult = $.fn.KnowhowDegreeOfDifficultyHitCountSumCalclator_toAp_getResult();
			$("#toAp_High").append('('+toApresult.High+')');
			$("#toAp_Middle").append('('+toApresult.Middle+')');
			$("#toAp_Low2").append('('+toApresult.Low2+')');
			$("#toAp_Low1").append('('+toApresult.Low1+')');
			num = parseInt(toApresult.Unknown1) + parseInt(toApresult.Unknown2);
			$("#toAp_Unknown").append('('+num+')');
			num = toApresult.High + toApresult.Middle
							+ toApresult.Low2 + toApresult.Low1
							+ toApresult.Unknown1
							+ toApresult.Unknown2
			$("#toAp_Total").append('('+num+')');
			//
			var factorToApresult = $.fn.KnowhowFactorSumCalclator_toAp_getResult();
			$("#toAp_factor1").text(factorToApresult.JavaEESpecChange
									+ factorToApresult.JavaVersionUpgradeChange
									+ factorToApresult.DBMSChange
									+ factorToApresult.APlibrary);
			$("#toAp_factor2").text(factorToApresult.ApServerDependsDepricatedChange);
			$("#toAp_factor3").text(factorToApresult.ApServerDependsChange + factorToApresult.WeblogicSpecChange);
			
			var factorToApresult = $.fn.ApServerKnowhowFactorHitCountSumCalclator_getResult();
			$("#toAp_factor1").append('('+factorToApresult.javaAndJavaEESpecChange+')');
			$("#toAp_factor2").append('('+factorToApresult.ApServerDepricatedChange+')');
			$("#toAp_factor3").append('('+factorToApresult.ApServerSpecficChange+')');
			//
			var fileFromApResult = $.fn.KnowhowFileCategorySumCalclator_fromAp_getResult();
			$("#fromAp_Java").text(fileFromApResult.java);
			$("#fromAp_Jsp").text(fileFromApResult.jsp);
			$("#fromAp_Xml").text(fileFromApResult.xml);
			$("#fromAp_Properties").text(fileFromApResult.properties);
			
			//
			var fileToApResult = $.fn.KnowhowFileCategorySumCalclator_toAp_getResult();
			$("#toAp_Java").text(fileToApResult.java);
			$("#toAp_Jsp").text(fileToApResult.jsp);
			$("#toAp_Xml").text(fileToApResult.xml);
			$("#toAp_Properties").text(fileToApResult.properties);
			
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
		var listOjb = $.fn.KnowhowFactorGraphSumCalclator_graph_getResult();
		var graph = Flotr.draw(container, listOjb, {
			legend : {
				backgroundColor : '#D2E8FF' // Light blue 
			},
			bars : {
				show : true,
				stacked : true,
				horizontal : horizontal,
				barWidth : 0.6,
				lineWidth : 1,
				shadowSize : 0
			},
			grid : {
				verticalLines : horizontal,
				horizontalLines : !horizontal
			},
			xaxis : {
				ticks : [ [ 0, "Todo your Edit (ex. WebLogic Version up)" ], [ 1, "Porting to JBossEAP" ] ]
			},
		});
	}catch(e){
		
	}
	
})(document.getElementById("graph"));