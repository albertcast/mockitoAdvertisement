package org.mps.board;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
Quiero probar la clase advertisementBoard -> no puede ser un mock
Tengo otras interfaces que influyen en el funcionamiento del advertisementBoard pero no son las que quiero probar -> hago mock.
 */

class AdvertisementBoardTest {

    AdvertisementBoard advertisementBoard;

    @BeforeEach
    void Inicio(){
        advertisementBoard = new AdvertisementBoard();
    }

    @AfterEach
    void Final(){
        advertisementBoard = null;
    }

    @Test
    void AdvertisementBoard_InitialBoard_HasOneAd(){
        assertEquals(1, advertisementBoard.numberOfPublishedAdvertisements());
    }

    @Test
    void Publish_InitialBoard_HasTwoAds(){
        Advertisement ad = new Advertisement("Titulo", "Texto", "THE Company");
        AdvertiserDatabase adbd = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway pg = Mockito.mock(PaymentGateway.class);

        Mockito.when(adbd.advertiserIsRegistered(ad.advertiser)).thenReturn(true);
        Mockito.when(pg.advertiserHasFunds(ad.advertiser)).thenReturn(true);

        advertisementBoard.publish(ad,adbd,pg);
        assertEquals(2, advertisementBoard.numberOfPublishedAdvertisements());
    }

    @Test
    void Publish_AdvertiserInDatabaseNoBalance_AdNotAdded(){
        Advertisement ad = new Advertisement("Titulo", "Texto", "Pepe Gotera y Otilio");
        AdvertiserDatabase adbd = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway pg = Mockito.mock(PaymentGateway.class);

        Mockito.when(adbd.advertiserIsRegistered(ad.advertiser)).thenReturn(true);
        Mockito.when(pg.advertiserHasFunds(ad.advertiser)).thenReturn(false);

        advertisementBoard.publish(ad, adbd, pg);
        assertEquals(1, advertisementBoard.numberOfPublishedAdvertisements());
    }

    @Test
    void Publish_AdvertiserInDatabaseWithBalance_AdvertiserCharged() {
        Advertisement ad = new Advertisement("Titulo", "Texto", "Robin Robot");
        AdvertiserDatabase adbd = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway pg = Mockito.mock(PaymentGateway.class);

        Mockito.when(adbd.advertiserIsRegistered(ad.advertiser)).thenReturn(true);
        Mockito.when(pg.advertiserHasFunds(ad.advertiser)).thenReturn(true);


        advertisementBoard.publish(ad, adbd, pg);

        Mockito.verify(pg, Mockito.times(1)).chargeAdvertiser(ad.advertiser);
    }

    @Test
    void Remove_AdvertisementBoardWithTwoAds_OneAdRemaining(){
        Advertisement ad = new Advertisement("Titulo", "Texto", "THE Company");
        Advertisement ad2 = new Advertisement("Title", "Text", "THE Company");
        AdvertiserDatabase adbd = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway pg = Mockito.mock(PaymentGateway.class);

        Mockito.when(adbd.advertiserIsRegistered(ad.advertiser)).thenReturn(true);
        Mockito.when(pg.advertiserHasFunds(ad.advertiser)).thenReturn(true);

        advertisementBoard.publish(ad, adbd, pg);
        advertisementBoard.publish(ad2, adbd, pg);
        advertisementBoard.deleteAdvertisement(ad.title, ad.advertiser);

        assertEquals(Optional.empty(), advertisementBoard.findByTitle("Titulo"));

    }

    @Test
    void Publish_PublishAgainSameAd_NotPublishedAndNotCharged(){
        Advertisement ad = new Advertisement("Titulo", "Texto", "Robin Robot");
        Advertisement ad2 = new Advertisement("Titulo", "Text", "Robin Robot");
        AdvertiserDatabase adbd = Mockito.mock(AdvertiserDatabase.class);
        PaymentGateway pg = Mockito.mock(PaymentGateway.class);

        Mockito.when(adbd.advertiserIsRegistered(ad.advertiser)).thenReturn(true);
        Mockito.when(pg.advertiserHasFunds(ad.advertiser)).thenReturn(true);

        advertisementBoard.publish(ad, adbd, pg);
        advertisementBoard.publish(ad2, adbd, pg);

        assertEquals(2, advertisementBoard.numberOfPublishedAdvertisements());
        Mockito.verify(pg, Mockito.times(1)).chargeAdvertiser("Robin Robot");

    }


}