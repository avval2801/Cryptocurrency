package DSCoinPackage;
import java.util.*;
import HelperClasses.*;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
      Members Moderator = new Members();
      Moderator.UID = "Moderator";
      ArrayList<Transaction> tras = new ArrayList<>();
      int coin = 100000;
      for(int i=0; i<coinCount; i++)
      {
          Members curr = DSObj.memberlist[i%DSObj.memberlist.length];
          Transaction t = new Transaction();
          t.Source = Moderator;
          t.Destination = curr;
          t.coinsrc_block = null;
          t.coinID = Integer.toString(coin);
          tras.add(t);
          DSObj.latestCoinID = Integer.toString(coin);
          coin++;
      }
      int numBlocks = coinCount/DSObj.bChain.tr_count;
      for(int i=0; i<numBlocks; i++)
      {
          Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
          for(int j=0; j<DSObj.bChain.tr_count; j++)
          {
              trarray[j] = tras.get(i*DSObj.bChain.tr_count+j);
          }
          TransactionBlock tB = new TransactionBlock(trarray);
          DSObj.bChain.InsertBlock_Honest(tB);
          for(int j=0;j<DSObj.bChain.tr_count; j++)
          {
              Pair<String,TransactionBlock> u = new Pair<>(null,null);
              u.first = tras.get(i*DSObj.bChain.tr_count+j).coinID;
              u.second = tB;
              for(int k=0; k<DSObj.memberlist.length; k++)
              {
                  if(DSObj.memberlist[k].UID.equals(tras.get(i*DSObj.bChain.tr_count+j).Destination.UID))
                          {
                              DSObj.memberlist[k].mycoins.add(u);
                          }
              }
          }
      }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
      Members Moderator = new Members();
      Moderator.UID = "Moderator";
      ArrayList<Transaction> tras = new ArrayList<>();
      int coin = 100000;
      for(int i=0; i<coinCount; i++)
      {
          Members curr = DSObj.memberlist[i%DSObj.memberlist.length];
          Transaction t = new Transaction();
          t.Source = Moderator;
          t.Destination = curr;
          t.coinsrc_block = null;
          t.coinID = Integer.toString(coin);
          tras.add(t);
          DSObj.latestCoinID = Integer.toString(coin);
          coin++;
      }
      int numBlocks = coinCount/DSObj.bChain.tr_count;
      for(int i=0; i<numBlocks; i++)
      {
          Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
          for(int j=0; j<DSObj.bChain.tr_count; j++)
          {
              trarray[j] = tras.get(i*DSObj.bChain.tr_count+j);
          }
          TransactionBlock tB = new TransactionBlock(trarray);
          DSObj.bChain.InsertBlock_Malicious(tB);
          for(int j=0;j<DSObj.bChain.tr_count; j++)
          {
              Pair<String,TransactionBlock> u = new Pair<>(null,null);
              u.first = tras.get(i*DSObj.bChain.tr_count+j).coinID;
              u.second = tB;
              for(int k=0; k<DSObj.memberlist.length; k++)
              {
                  if(DSObj.memberlist[k].UID.equals(tras.get(i*DSObj.bChain.tr_count+j).Destination.UID))
                          {
                              DSObj.memberlist[k].mycoins.add(u);
                          }
              }
          }
      }
  }
}
