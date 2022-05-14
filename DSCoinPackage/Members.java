package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
      Pair<String, TransactionBlock> p = new Pair<>(null,null);
      p = this.mycoins.remove(0);
      Transaction tobj = new Transaction();
      tobj.Source = this;
      tobj.coinID = p.first;
      tobj.coinsrc_block = p.second;
      for(int i=0; i<DSobj.memberlist.length; i++)
      {
          if(DSobj.memberlist[i].UID.equals(destUID))
          {
              tobj.Destination = DSobj.memberlist[i];
              break;
          }
      }
      int i=0;
      while(this.in_process_trans[i]!=null)
      {
          i++;
      }
      this.in_process_trans[i] = tobj;
      DSobj.pendingTransactions.AddTransactions(tobj);
  }
  
  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
      Pair<String, TransactionBlock> p = new Pair<>(null,null);
      p = this.mycoins.remove(0);
      Transaction tobj = new Transaction();
      tobj.Source = this;
      tobj.coinID = p.first;
      tobj.coinsrc_block = p.second;
      for(int i=0; i<DSobj.memberlist.length; i++)
      {
          if(DSobj.memberlist[i].UID.equals(destUID))
          {
              tobj.Destination = DSobj.memberlist[i];
              break;
          }
      }
      int i=0;
      while(this.in_process_trans[i]!=null)
      {
          i++;
      }
      this.in_process_trans[i] = tobj;
      DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    TransactionBlock curr = DSObj.bChain.lastBlock;
    while(curr!=null)
    {
        boolean found = false;
        for(int i=0; i<curr.trarray.length; i++)
        {
            if(tobj.equals(curr.trarray[i]))
            {
                found = true;
                break;
            }
        }
        if(found)
        {
            break;
        }
        else
        {
            curr=curr.previous;
        }
    }
    if(curr==null)
    {
        throw new MissingTransactionException();
    }
    ArrayList<Pair<String,String>> path = new ArrayList<>();
    TreeNode node = new TreeNode();
    node = curr.Tree.findNode(tobj, curr.Tree.rootnode);
    while(node.parent!=null)
    {
        Pair<String,String> p = new Pair<>("","");
        if(node.parent.left.equals(node))
        {
            p.first = node.val;
            p.second = node.parent.right.val;
        }
        else
        {
            p.first = node.parent.left.val;
            p.second = node.val;
        }
        node = node.parent;
        path.add(p);
    }
    Pair<String,String> p = new Pair<>(node.val,null);
    path.add(p);
    
    ArrayList<Pair<String,String>> block = new ArrayList<>();
    String prev;
    if(curr.previous==null)
    {
        prev = "DSCoin";
    }
    else
    {
        prev = curr.previous.dgst;
    }
    Pair<String,String> blo = new Pair<>(null,null);
    Pair<String,String> blo2 = new Pair<>(null,null);
    blo.first = prev;
    blo.second = null;
    block.add(blo);
    blo2.first = curr.dgst;
    blo2.second = prev+"#"+curr.trsummary+"#"+curr.nonce;
    block.add(blo2);
    
    TransactionBlock last = DSObj.bChain.lastBlock;
    int k = 0;
    while(!last.equals(curr))
    {
        Pair<String,String> h = new Pair<>("","");
        block.add(h);
        k++;
        last = last.previous;
    }
    last = DSObj.bChain.lastBlock;
    for(int i=0; i<k; i++)
    {
        Pair<String,String> t = new Pair<>("","");
        t.first = last.dgst;
        t.second = last.previous.dgst+"#"+last.trsummary+"#"+last.nonce;
        block.set(k+1-i,t);
        last = last.previous;
    }
    
    Pair<List<Pair<String, String>>, List<Pair<String, String>>> ans = new Pair<>(path,block);
    int i=0;
    boolean found = false;
    while(!found)
    {
        if(this.in_process_trans[i]==null)
        {
            i++;
        }
        else
        {
            if(this.in_process_trans[i].equals(tobj))
            {
                found = true;
            }
            else
            {
                i++;
            }
        }
    }
    this.in_process_trans[i]=null;
    Pair<String,TransactionBlock> u = new Pair<>(tobj.coinID,curr);
    if(tobj.Destination.mycoins.size()==0)
    {
        tobj.Destination.mycoins.add(u);
    }
    else
    {
        int j=0;
        while(Integer.parseInt(tobj.Destination.mycoins.get(j).first)<Integer.parseInt(tobj.coinID))
        {
            j++;
        }
        tobj.Destination.mycoins.add(j,u);
    }
    return ans;
  }

  public void MineCoin(DSCoin_Honest DSObj) {
      Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
      int added=0;
      while(added!=DSObj.bChain.tr_count-1)
      {
          Transaction curr = new Transaction();
          try
          {
              curr = DSObj.pendingTransactions.RemoveTransaction();
          }
          catch(EmptyQueueException en)
          {
              System.out.println(en);
          }
          if(!CheckTransactionStaticH(curr,DSObj))
          {
              continue;
          }
          boolean copy = false;
          for(int i=0; i<added; i++)
          {
              if(trarray[i].coinID.equals(curr.coinID))
              {
                  copy = true;
                  break;
              }
          }
          if(copy)
          {
              continue;
          }
          trarray[added]=curr;
          added++;
      }
      
      Transaction minerRewardTransaction = new Transaction();
      int last = Integer.parseInt(DSObj.latestCoinID)+1;
      DSObj.latestCoinID = Integer.toString(last);
      minerRewardTransaction.coinID = DSObj.latestCoinID;
      minerRewardTransaction.Source = null;
      minerRewardTransaction.Destination = this;
      minerRewardTransaction.coinsrc_block = null;
      trarray[added] = minerRewardTransaction;
      TransactionBlock tB = new TransactionBlock(trarray);
      DSObj.bChain.InsertBlock_Honest(tB);
      Pair<String,TransactionBlock> ne = new Pair<>(minerRewardTransaction.coinID,tB);
      this.mycoins.add(ne);
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
      Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
      int added=0;
      while(added!=DSObj.bChain.tr_count-1)
      {
          Transaction curr = new Transaction();
          try
          {
              curr = DSObj.pendingTransactions.RemoveTransaction();
          }
          catch(EmptyQueueException en)
          {
              System.out.println(en);
          }
          
          if(!CheckTransactionStaticM(curr,DSObj))
          {
              continue;
          }
          boolean copy = false;
          for(int i=0; i<added; i++)
          {
              if(trarray[i].coinID.equals(curr.coinID))
              {
                  copy = true;
                  break;
              }
          }
          if(copy)
          {
              continue;
          }
          trarray[added]=curr;
          added++;
      }
      Transaction minerRewardTransaction = new Transaction();
      int last = Integer.parseInt(DSObj.latestCoinID)+1;
      DSObj.latestCoinID = Integer.toString(last);
      minerRewardTransaction.coinID = DSObj.latestCoinID;
      minerRewardTransaction.Source = null;
      minerRewardTransaction.Destination = this;
      minerRewardTransaction.coinsrc_block = null;
      trarray[added] = minerRewardTransaction;
      TransactionBlock tB = new TransactionBlock(trarray);
      DSObj.bChain.InsertBlock_Malicious(tB);
      Pair<String,TransactionBlock> ne = new Pair<>(minerRewardTransaction.coinID,tB);
      this.mycoins.add(ne);
  }  
  public static boolean CheckTransactionStaticH(Transaction t,DSCoin_Honest DSObj)
  {
    boolean src_found = false;
    if(t.coinsrc_block == null)
    {
        return true;
    }
    for(int i=0; i<t.coinsrc_block.trarray.length; i++)
    {
        if(t.coinsrc_block.trarray[i].coinID.equals(t.coinID) && t.coinsrc_block.trarray[i].Destination.UID.equals(t.Source.UID))
        {
            src_found = true;
            break;
        }
    }
    if(!src_found)
    {
        return false;
    }
    TransactionBlock bw = DSObj.bChain.lastBlock;
    while(!bw.equals(t.coinsrc_block))
    {
        for(int i=0; i<bw.trarray.length; i++)
        {
            if(bw.trarray[i].coinID.equals(t.coinID))
            {
                return false;
            }
        }
        bw = bw.previous;
    }
    return true;
  }
  public static boolean CheckTransactionStaticM(Transaction t,DSCoin_Malicious DSObj)
  {
    boolean src_found = false;
    if(t.coinsrc_block == null)
    {
        return true;
    }
    for(int i=0; i<t.coinsrc_block.trarray.length; i++)
    {
        if(t.coinsrc_block.trarray[i].coinID.equals(t.coinID) && t.coinsrc_block.trarray[i].Destination.UID.equals(t.Source.UID))
        {
            src_found = true;
            break;
        }
    }
    if(!src_found)
    {
        return false;
    }
    TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();
    TransactionBlock bw = lastBlock;
    while(!bw.equals(t.coinsrc_block))
    {
        for(int i=0; i<bw.trarray.length; i++)
        {
            if(bw.trarray[i].coinID.equals(t.coinID))
            {
                return false;
            }
        }
        bw = bw.previous;
    }
    return true;
  }
}
