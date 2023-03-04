package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user=userRepository2.findById(userId).get();
        if (user.getServiceProviderList().size()>=1){
            throw new Exception("Already connected");
        }
        if (user.getOriginalCountry().getCountryName().toString().equalsIgnoreCase(countryName)){
            return user;
        }
        List<ServiceProvider> serviceProviders=user.getServiceProviderList();
        if (serviceProviders.size()==0){
            throw new Exception("Unable to connect");
        }
        int min=Integer.MAX_VALUE;
        ServiceProvider sp=null;
        Country c=null;
        for (ServiceProvider serviceProvider:serviceProviders){
            List<Country> countryList=serviceProvider.getCountryList();
            for (Country country:countryList){
                if(country.getCountryName().toString().equalsIgnoreCase(countryName)){
                    if(serviceProvider.getId()<min){
                        min=serviceProvider.getId();
                        sp=serviceProvider;
                        c=country;
                        break;
                    }
                }
            }
        }
        if (sp==null){
            throw new Exception("Unable to connect");
        }
        Connection connection=new Connection();
        connection.setUser(user);
        connection.setServiceProvider(sp);
        connection=connectionRepository2.save(connection);

        String m_ip=c.getCode()+"."+sp.getId()+"."+userId;
        user.setMaskedIP(m_ip);
        user.setConnected(true);
        user.getConnectionList().add(connection);

        sp.getConnectionList().add(connection);

        userRepository2.save(user);
        serviceProviderRepository2.save(sp);
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIP(null);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();
        String code="";
//        CountryName countryName=null;
        if(receiver.getConnected()){
            code=receiver.getMaskedIP().substring(0,3);
        }
        else {
            code=receiver.getOriginalCountry().getCode();
        }
        if(sender.getOriginalCountry().getCode().equals(code)){
            return sender;
        }
        CountryName countryName=null;
        if(CountryName.IND.toCode().equals(code)){
            countryName=CountryName.IND;
        }
        else if(CountryName.USA.toCode().equals(code)){
            countryName=CountryName.USA;
        }
        else if(CountryName.AUS.toCode().equals(code)){
            countryName=CountryName.AUS;
        }
        else if(CountryName.CHI.toCode().equals(code)){
            countryName=CountryName.CHI;
        }
        else if(CountryName.JPN.toCode().equals(code)){
            countryName=CountryName.JPN;
        }
        try {
            User user=connect(senderId, countryName.toString());
            return user;
        }
        catch (Exception e){
            throw new Exception("Cannot establish communication");
        }

    }
}
