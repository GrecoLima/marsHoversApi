package com.marshoversapi.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.marshoversapi.repository.PreferencesRepository;
import com.marshoversapi.response.MarsPhoto;
import com.marshoversapi.response.MarsRoverApiResponse;
import com.marshoversapi.vo.HomeVO;

@Service
public class MarsRoverApiService {

  private static final String API_KEY = "vPrM8dJbbKfqTVZKZ4b7EmReNaofOnGcbpn37xTf";
  
  private Map<String, List<String>> validCameras = new HashMap<>();
  
  @Autowired
  private PreferencesRepository preferencesRepo;
  
  public MarsRoverApiService () {
    validCameras.put("Opportunity", Arrays.asList("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES"));
    validCameras.put("Curiosity", Arrays.asList("FHAZ", "RHAZ", "MAST", "CHEMCAM", "MAHLI", "MARDI", "NAVCAM"));
    validCameras.put("Spirit", Arrays.asList("FHAZ", "RHAZ", "NAVCAM", "PANCAM", "MINITES"));
  }
  
  public MarsRoverApiResponse getRoverData(HomeVO homeVO) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    RestTemplate rt = new RestTemplate();
    
    List<String> apiUrlEnpoints = getApiUrlEnpoints(homeVO);
    List<MarsPhoto> photos = new ArrayList<>();
    MarsRoverApiResponse response = new MarsRoverApiResponse();
    
    apiUrlEnpoints.stream()
                  .forEach(url -> { 
                    MarsRoverApiResponse apiResponse = rt.getForObject(url, MarsRoverApiResponse.class);
                    photos.addAll(apiResponse.getPhotos());
                  });
    
    response.setPhotos(photos);
    
    return response;
  }
  
  public List<String> getApiUrlEnpoints (HomeVO homeVO) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    List<String> urls = new ArrayList<>();
    
    Method[] methods = homeVO.getClass().getMethods();
    
    // This code will grab all getCamera* methods and (if value returns true) then we will build a API URL to
    //  call in order to fetch pictures for a given rover / camera / sol.
    for (Method method : methods) {
      if (method.getName().indexOf("getCamera") > -1 && Boolean.TRUE.equals(method.invoke(homeVO))) {
        String cameraName = method.getName().split("getCamera")[1].toUpperCase();
        if (validCameras.get(homeVO.getMarsApiRoverData()).contains(cameraName)) {
          urls.add("https://api.nasa.gov/mars-photos/api/v1/rovers/"+homeVO.getMarsApiRoverData()+"/photos?sol="+homeVO.getMarsSol()+"&api_key=" + API_KEY + "&camera=" + cameraName);
        }
      }
    }
    
    return urls;
  }

  public Map<String, List<String>> getValidCameras() {
    return validCameras;
  }

  public HomeVO save(HomeVO homeVO) {
    return preferencesRepo.save(homeVO);
  }

  public HomeVO findByUserId(Long userId) {
    return preferencesRepo.findByUserId(userId);
  }
}
