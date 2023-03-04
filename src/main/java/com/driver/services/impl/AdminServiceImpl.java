package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin=new Admin();
        admin.setPassword(password);
        admin.setUsername(username);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        ServiceProvider serviceProvider=new ServiceProvider();
        Admin admin=adminRepository1.findById(adminId).get();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        if(!countryName.equalsIgnoreCase("ind") && !countryName.equalsIgnoreCase("jpn")
        && !countryName.equalsIgnoreCase("aus") && !countryName.equalsIgnoreCase("usa")
        && !countryName.equalsIgnoreCase("chi")){
            throw new Exception("Country not found");
        }
        Country country=new Country();
        ServiceProvider serviceProvider=serviceProviderRepository1.findById(serviceProviderId).get();
        if(CountryName.IND.toString().equalsIgnoreCase(countryName)){
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }
        if(CountryName.USA.toString().equalsIgnoreCase(countryName)){
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        if(CountryName.AUS.toString().equalsIgnoreCase(countryName)){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }
        if(CountryName.CHI.toString().equalsIgnoreCase(countryName)){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }
        if(CountryName.JPN.toString().equalsIgnoreCase(countryName)){
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        country.setServiceProvider(serviceProvider);
        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
    }
}
