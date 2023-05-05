package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.driver.model.SubscriptionType.*;

@Service
public class SubscriptionService {

  //  private static final SubscriptionType BASIC = ;
    //  private static final Object PRO = ;
    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay


        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);
        int amount=0;
        if(subscriptionEntryDto.getSubscriptionType().equals(BASIC)){
            amount=500+200* subscriptionEntryDto.getNoOfScreensRequired();
        }else if(subscriptionEntryDto.getSubscriptionType().equals(PRO)){
            amount=800+250*subscriptionEntryDto.getNoOfScreensRequired();
        }else{
            amount=1000+350*subscriptionEntryDto.getNoOfScreensRequired();
        }
        subscription.setTotalAmountPaid(amount);
        user.setSubscription(subscription);
        userRepository.save(user);
        return amount;


    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user=userRepository.findById(userId).get();
        Subscription subscription=user.getSubscription();
        if(subscription.getSubscriptionType().equals(ELITE)){
            throw new Exception("Already the best Subscription");
        }else if(subscription.getSubscriptionType().equals(PRO)){
            subscription.setSubscriptionType(ELITE);
            subscription.setTotalAmountPaid(1000+350*subscription.getNoOfScreensSubscribed());
            user.setSubscription(subscription);
            userRepository.save(user);
            return 200+100*subscription.getNoOfScreensSubscribed();
        }else{
            subscription.setSubscriptionType(PRO);
            subscription.setTotalAmountPaid(800+250*subscription.getNoOfScreensSubscribed());
            user.setSubscription(subscription);
            userRepository.save(user);
            return 300+50*subscription.getNoOfScreensSubscribed();
        }



    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions=subscriptionRepository.findAll();
        int revenue=0;
        for(Subscription sub:subscriptions){
            revenue+=sub.getTotalAmountPaid();
        }

        return revenue;

    }

}
