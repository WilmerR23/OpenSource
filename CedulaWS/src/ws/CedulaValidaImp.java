package ws;
import javax.jws.WebService;

@WebService(endpointInterface="ws.CedulaValida")
public class CedulaValidaImp implements CedulaValida {

	@Override
	public boolean CedulaEsValida(String pCedula) 
    {
        int vnTotal = 0;
        String vcCedula = pCedula.replace("-", "");
        int pLongCed = vcCedula.trim().length();
        int[] digitoMult = new int[] { 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1 };

        if (pLongCed < 11 || pLongCed > 11)
            return false;

        for (int vDig = 1; vDig <= pLongCed; vDig++)
        {
            int vCalculo = Integer.parseInt(vcCedula.substring(vDig - 1, vDig)) * digitoMult[vDig - 1];

            if (vCalculo < 10)
                vnTotal += vCalculo;
            else
                vnTotal += Integer.parseInt(String.valueOf(vCalculo).substring(0, 1)) + Integer.parseInt(String.valueOf(vCalculo).substring(1, 2));
        }

        if (vnTotal % 10 == 0)
            return true;
        else
            return false;
    }

}
