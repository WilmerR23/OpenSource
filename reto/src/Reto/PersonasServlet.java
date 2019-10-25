package Reto;

import javax.servlet.annotation.WebServlet;

@WebServlet("/personas/*")
public class PersonasServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
    public void initEntity() {
        try {
            super.entity = new Personas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
