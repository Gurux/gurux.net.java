/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gurux.net;

/**
 *
 * @author Gurux Ltd
 */
class GXSync 
{
    int[] Count;
    /*
     * Constructor.
     */
    public GXSync(int[] cnt)
    {
        Count = cnt;
        ++Count[0];
    }
    
    /*
     * Destructor.
     */
    @Override
    protected void finalize() throws Throwable
    {
      --Count[0];      
      super.finalize(); //not necessary if extending Object.
    }     
}
