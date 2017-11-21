package nl.cgi.hackaton

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.Theme
import com.vaadin.annotations.VaadinServletConfiguration
import com.vaadin.data.Result
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinServlet
import com.vaadin.ui.*
import elemental.json.JsonObject
import nl.cgi.hackaton.domain.Series
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.PostMethod
import java.io.InputStream
import java.net.URI
import java.net.URL

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 *
 *
 * The UI is initialized using [.init]. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */

@Theme("mytheme")
class MyUI : UI() {

    override fun init(vaadinRequest: VaadinRequest) {
        val layout = VerticalLayout()

        val name = TextField()
        name.caption = "Name:"

        val description = TextField()
        description.caption = "Description:"

    //    data class PersonInfo(val name: String, val address: String)

        val result = URL("http://localhost:8090/series").readText()

        val mapper = jacksonObjectMapper()
        val series: Array<Series> = mapper.readValue(result)

        //val serieArea = TextArea()
        //series.forEach{ serie -> serieArea.value = serieArea.value + "\n" + serie.name}

        val gridLayout = GridLayout(4, series.size + 1);
        val header1 = Label()
        header1.value = "Id"
        header1.setWidth("100")
        val header2 = Label()
        header2.value = "Name"
        header2.setWidth("300")
        val header3 = Label()
        header3.value = "Description"
        header3.setWidth("400")
        gridLayout.addComponent(header1, 0, 0)
        gridLayout.addComponent(header2, 1, 0)
        gridLayout.addComponent(header3, 2, 0)

        series.forEachIndexed { index, serie ->
            val label1 = Label()
            label1.value = serie.id.toString()
            val label2 = Label()
            label2.value = serie.name
            val label3 = Label()
            label3.value = serie.description
            gridLayout.addComponent(label1, 0, index + 1)
            gridLayout.addComponent(label2, 1, index + 1)
            gridLayout.addComponent(label3, 2, index + 1)
        }



        val button = Button("Add")
        button.addClickListener { e ->
            val serie: Series = Series()
            serie.name = name.value
            serie.description = description.value
            //series.plusElement(serie)
            val post = PostMethod("http://localhost:8090/serie")
//            val data: Array<NameValuePair> = Array<NameValuePair>(2, )
            val namevalue1: NameValuePair = NameValuePair("name", name.value)
            val namevalue2: NameValuePair = NameValuePair("description", description.value)

            val data: Array<NameValuePair> = arrayOf(namevalue1, namevalue2)

           //  data.plusElement(namevalue1)
           //  data.plusElement(namevalue2);

            post.setRequestBody(data);
            val input: InputStream = post.getResponseBodyAsStream();
            layout.addComponent(Label(name.value + ", added!"))
        }

        layout.addComponents(name, description, button, gridLayout)

        content = layout
    }

    @WebServlet(urlPatterns = arrayOf("/*"), name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI::class, productionMode = false)
    class MyUIServlet : VaadinServlet()
}
