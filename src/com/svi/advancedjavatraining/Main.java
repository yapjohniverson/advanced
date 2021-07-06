package com.svi.advancedjavatraining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.svi.advancedjavatraining.object.City;
import com.svi.advancedjavatraining.object.PopulationData;
import com.svi.advancedjavatraining.object.Province;
import com.svi.advancedjavatraining.utils.JSONFileReader;
import com.svi.advancedjavatraining.utils.PopulationFileWriter;
import com.svi.advancedjavatraining.utils.WebLoader;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		long startTime = System.nanoTime();

		// TODO WORK AREA
		WebLoader webLoader = new WebLoader();
		PopulationFileWriter storeMatchingData = new PopulationFileWriter();
		List<City> cityInfo = new ArrayList<City>();
		
		List<Province> provinceInfo = new ArrayList<Province>();
		
		
		Thread t = new Thread() {
			public void run() {
				try {
					
						cityInfo.addAll(Collections.synchronizedList(webLoader.getCities()));
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		Thread t2 = new Thread() {
			public void run() {
				try {
				
						provinceInfo.addAll(Collections.synchronizedList(webLoader.getProvinces()));
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
	
		t.start();
		
		
	
		t2.start();

		
		
		t.join();
		t2.join();
		
		
		System.out.println(cityInfo.size());
		System.out.println(provinceInfo.size());

	
		final AtomicInteger totalPopulation = new AtomicInteger();
		Thread []thread = new Thread[cityInfo.size()];
		
		for (int i = 0; i < cityInfo.size(); i++) {
			int index = i;
			 thread[index] = new Thread() {
				 
				 public void run() {
					 
						for(int j = 0; j < provinceInfo.size(); j++) {
							if(cityInfo.get(index).getProvince() != null) { //nullpointerexception...
								if(cityInfo.get(index).getProvince().equals(provinceInfo.get(j).getKey())) {
									JSONFileReader fileReader = new JSONFileReader(provinceInfo.get(j).getName(),cityInfo.get(index).getName());
									try {
										int populationCount=0;
										if(!(fileReader.getData() == null)) {
											populationCount = (int) fileReader.getData().getPopulation();
											totalPopulation.addAndGet(populationCount);
											PopulationData popData = new PopulationData(provinceInfo.get(j).getName(), cityInfo.get(index).getName(), populationCount);
											synchronized (Main.class) {
												storeMatchingData.addPopulationRecord(popData);
											}
										}else if(fileReader.getData() == null) {
											PopulationData popData = new PopulationData(provinceInfo.get(j).getName(), cityInfo.get(index).getName(), populationCount);
											synchronized (Main.class) {
												storeMatchingData.addPopulationRecord(popData);
											}
										}
									}catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							
						}
					}
				};
			thread[index].start();
				 
			 }
		for(int x=0; x<thread.length; x++) {
			thread[x].join();
		
		}
		// add 1 more row for total population
		
		System.out.println("TOTAL POPULATION " + totalPopulation);

		storeMatchingData.writeToFile();

		// TODO END WORK AREA
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		long millis = TimeUnit.NANOSECONDS.toMillis(totalTime);

		long seconds = TimeUnit.NANOSECONDS.toSeconds(totalTime);

		long minutes = TimeUnit.NANOSECONDS.toMinutes(totalTime);

		long hours = TimeUnit.NANOSECONDS.toHours(totalTime);

		String time = String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
		System.out.println(time);

	}

}
