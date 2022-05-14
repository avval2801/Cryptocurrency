package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
      this.Tree = new MerkleTree();
      Transaction[] fill = new Transaction[t.length];
      for(int i=0;i<fill.length; i++)
      {
          fill[i]=t[i];
      }
      this.trarray = fill;
      this.previous = null;
      this.Tree.Build(fill);
      this.trsummary = this.Tree.rootnode.val;
      this.dgst = null;
  }

  public boolean checkTransaction (Transaction t) {
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
    TransactionBlock bw = this.previous;
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
