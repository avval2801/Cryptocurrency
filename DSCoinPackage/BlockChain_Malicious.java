package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;
import java.util.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    String prev;
    CRF newCRF = new CRF(64);
    if(tB==null)
    {
        return false;
    }
    if(tB.previous==null)
    {
        prev = start_string;
    }
    else
    {
        prev = tB.previous.dgst;
    }
    if(!tB.dgst.substring(0,4).equals("0000") || !tB.dgst.equals(newCRF.Fn(prev+"#"+tB.trsummary+"#"+tB.nonce)))
    {
        return false;
    }
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    if(!tB.trsummary.equals(tree.rootnode.val))
    {
        return false;
    }
    for(int i=0; i<tB.trarray.length; i++)
    {
        if(!tB.checkTransaction(tB.trarray[i]))
        {
            return false;
        }
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    boolean empty = true;
    for(int i=0; i<this.lastBlocksList.length; i++)
    {
        if(this.lastBlocksList!=null)
        {
            empty = false;
            break;
        }
    }
    if(empty)
    {
        return null;
    }
    ArrayList<TransactionBlock> ends =   new ArrayList<>();
    ArrayList<TransactionBlock> means = new ArrayList<>();
    for(int i=0; i<this.lastBlocksList.length; i++)
    {
        if(this.lastBlocksList[i]==null)
        {
            continue;
        }
        if(checkTransactionBlock(this.lastBlocksList[i]))
        {
            means.add(this.lastBlocksList[i]);
        }
        TransactionBlock curr = this.lastBlocksList[i];
        while(curr!=null)
        {
            if(!checkTransactionBlock(curr) && checkTransactionBlock(curr.previous))
            {
                means.add(curr.previous);
            }
            curr = curr.previous;
        }
    }
    for(int i=0; i<means.size(); i++)
    {
        TransactionBlock curr = means.get(i);
        while(checkTransactionBlock(curr))
        {
            curr=curr.previous;
        }
        if(curr==null)
        {
            ends.add(means.get(i));
        }
    }
    int[] lengths = new int[ends.size()];
    for(int i=0; i<ends.size(); i++)
    {
        int j=0;
        TransactionBlock curr = ends.get(i);
        while(curr!=null)
        {
            curr = curr.previous;
            j++;
        }
        lengths[i] = j;
    }
    int max = 0;
    int maxindex =0;
    for(int i=0; i<ends.size();i++)
    {
        if(lengths[i]>max)
        {
            max=lengths[i];
            maxindex = i;
        }
    }
    if(ends.size()==0)
    {
        return null;
    }
    return ends.get(maxindex);
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
      CRF newCRF = new CRF(64);
      long nonce = 1000000001;
      TransactionBlock lastBlock = this.FindLongestValidChain();
      String prev;
      if(lastBlock==null)
      {
          prev = start_string;
      }
      else
      {
          prev = lastBlock.dgst;
      }
      String dgst;
      boolean flag = false;
      while(!flag)
      {
          String s = String.valueOf(nonce);
          dgst = newCRF.Fn(prev+"#"+newBlock.trsummary+"#"+s);
          if(dgst.substring(0,4).equals("0000"))
          {
              flag = true;
          }
          else
          {
              nonce++;
          }
      }
      newBlock.nonce = String.valueOf(nonce);
      newBlock.dgst = newCRF.Fn(prev+"#"+newBlock.trsummary+"#"+newBlock.nonce);
      newBlock.previous = lastBlock;
      boolean found = false;
      for(int i=0; i<this.lastBlocksList.length; i++)
      {
          if(this.lastBlocksList[i]==null)
          {
              continue;
          }
          if(this.lastBlocksList[i].equals(lastBlock))
          {
              found = true;
              lastBlocksList[i] = newBlock;
              break;
          }
      }
      if(!found)
      {
          for(int i=0; i<this.lastBlocksList.length; i++)
          {
              if(this.lastBlocksList[i]==null)
              {
                  this.lastBlocksList[i] = newBlock;
                  break;
              }
          }
      }
  }
}
