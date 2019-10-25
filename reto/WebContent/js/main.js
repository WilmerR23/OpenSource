var app = new Vue({
    el: '#app',
    data: {
        rows: [ 'Cedula', 'FechaNacimiento', 'Nombre', 'Opciones' ],
        personas: [],
        formbody: {
        	cedula: '',
        	fechaNacimiento: '',
        	nombre: ''
        },
        axs: null,
        current: null,
        currentOperation: "Crear"
    },
    mounted () {
        this.axs  = axios.create({
            baseURL: 'http://localhost:8282/reto'
        });
        this.axs.defaults.headers.post['Content-Type'] = 'application/x-www-formbody-urlencoded';
        this.fetch();
    },
    methods: {
        fetch() {
            this.axs.get('/personas')
            .then(
                response => (
                    this.personas = response.data
                )
            ) 
        },
        save(evt) {
            evt.preventDefault();
            url = '/personas';
            const formdata = new URLSearchParams();
            formdata.append('Nombre', this.formbody.nombre);
            formdata.append('Cedula', this.formbody.cedula);
            formdata.append('FechaNacimiento', this.formbody.fechaNacimiento);
            if (this.current != null) {
                formdata.append('id', this.current.Id);
                url = url + '/' + this.current.Id;
            }
            this.axs({
                method: 'post',
                url: url,
                data: formdata
            })
            .then(response => {
                this.fetch();
                alert(response.data);
                if (response.data == "Satisfactorio.") {
                    this.resetForm(evt);
                }
            });
        },
        resetForm(evt) {
            evt.preventDefault();
            this.formbody.nombre = '';
            this.formbody.cedula = '';
            this.formbody.fechaNacimiento = '';
            this.current = null;
            this.currentOperation = "Crear";
        },
        getPersona(persona) {
            this.axs.get('/personas/' + persona.Id)
            .then(
                response => {
                    this.formbody.nombre = response.data.Nombre;
                    this.formbody.cedula = response.data.Cedula;
                    this.formbody.fechaNacimiento = response.data.FechaNacimiento;
                    this.current = response.data;
                }
            )
            this.currentOperation = "Actualizar";
        },
        deletePersona(persona) {
            this.axs.delete('/personas/' + persona.Id)
            .then(
                response => {
                    if (response) {
                        this.fetch();
                    } else {
                        alert("Ha ocurrido un error");
                    }
                }
            ) 
        }
    }
});