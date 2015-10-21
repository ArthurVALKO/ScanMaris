package grib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class GribIO {
	
	
	
	public GribIO(String monPath){
		
		int NTIME = 0;
		int NLAT = 0;
		int NLON = 0;
		int strlen = 0;

		try {
		
			//variables temporaires 
			ArrayList < ArrayList <ArrayList <Double>> > windDirection_XYT = new ArrayList < ArrayList <ArrayList <Double>> >();
			ArrayList < ArrayList <ArrayList <Double>> > windSpeed_XYT = new ArrayList < ArrayList <ArrayList <Double>> >();
			ArrayList < Calendar > theDates = new ArrayList < Calendar >();
			double[] theLongitudes;
			double[] theLatitudes;
			
			NetcdfFile dataFile = NetcdfFile.open(System.getProperty("user.dir") + "/FichierVent/" + monPath, null);

			for(int i =0;i< dataFile.getDimensions().size();i++){
				if(dataFile.getDimensions().get(i).getFullName().equals("south_north")){
					NLON = dataFile.getDimensions().get(i).getLength();
				}
				if(dataFile.getDimensions().get(i).getFullName().equals("west_east")){
					NLAT = dataFile.getDimensions().get(i).getLength();
				}
				if(dataFile.getDimensions().get(i).getFullName().equals("Time")){
					NTIME = dataFile.getDimensions().get(i).getLength();
				}
				if(dataFile.getDimensions().get(i).getFullName().equals("DateStrLen")){
					strlen= dataFile.getDimensions().get(i).getLength();
				}
			}
			//on prend les variable
			Variable latVar = dataFile.findVariable("XLAT");
			if (latVar == null) {
				System.out.println("Cant find Variable latitude");
				return;
			}

			Variable lonVar = dataFile.findVariable("XLONG");
			if (lonVar == null) {
			System.out.println("Cant find Variable longitude");
			return;
		}
		Variable timeVar = dataFile.findVariable("Times");
		if (timeVar == null) {
			System.out.println("Cant find Variable Times");
			return;
		}

		Variable WdirVar = dataFile.findVariable("WDIR10");
		if (WdirVar == null) {
			System.out.println("Cant find Variable WindDirection");
			return;
		}
		Variable WspdVar = dataFile.findVariable("SPD10");
		if (WspdVar == null) {
			System.out.println("Cant find Variable WindDirection");
			return;
		}

		Array latArray =  latVar.read();
		Array lonArray =  lonVar.read();
		Array TimeArray =  timeVar.read();
		Array wdirArray =  WdirVar.read();
		Array wspdArray =  WspdVar.read();

		theLongitudes = new double[NLON];
		theLatitudes = new double[NLAT];

		for(int i =0; i< NLON;i++){
			theLongitudes[i] = (double)lonArray.getFloat(i);
		}
		for(int i =0; i< NLAT;i++){
			theLatitudes[i] = (double)latArray.getFloat(i*NLON);
		}
		for(int i =0; i< NTIME;i++){
			String time = "";
			for(int c =0; c<strlen;c++){
				time = time + String.valueOf(TimeArray.getChar(i*strlen+c));
			}
			Calendar timer = Calendar.getInstance();
			timer.set(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)), Integer.parseInt(time.substring(8, 10)), Integer.parseInt(time.substring(11, 13)), Integer.parseInt(time.substring(14, 16)), Integer.parseInt(time.substring(17)));
			theDates.add(timer);
		}

		//on prend les valeur de direction de vent et de vitesse de vent

		for(int temp = 0; temp <NTIME;temp++){

			ArrayList <ArrayList <Double>> windDirection_XY = new ArrayList <ArrayList <Double>>();
			ArrayList <ArrayList <Double>> windSpeed_XY = new ArrayList <ArrayList <Double>>();

			for(int lat =0; lat <NLAT;lat++){

				ArrayList <Double> windDirection_X = new ArrayList <Double>();
				ArrayList <Double> windSpeed_X = new ArrayList <Double>();

				for(int lon = 0; lon <NLON;lon++){

					double vitesseDuVent = wspdArray.getFloat(temp*NLAT*NLON + lat*NLON + lon);

					windSpeed_X.add(vitesseDuVent);
					windDirection_X.add((double)wdirArray.getFloat(temp*NLAT*NLON + lat*NLON + lon));
				}
				windSpeed_XY.add(windSpeed_X);
				windDirection_XY.add(windDirection_X);
			}
			windDirection_XYT.add(windDirection_XY);
			windSpeed_XYT.add(windSpeed_XY);
		}

    //thePrevision = new Prevision(theLongitudes,theLatitudes,windDirection_XYT,windSpeed_XYT,theDates);	
    
	} catch (IOException e) {
		e.printStackTrace();
	}

	}
}


