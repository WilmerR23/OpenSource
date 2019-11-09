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
        currentOperation: "Crear",
        map: null,
        markersArray: [],
        latitude: null,
        longitude: null,
        secondMap: null,
        markersSecondMapArray: [],
        markersInfoWindows: [],
        currentClicked: null,
        directionsService: new google.maps.DirectionsService(),
        directionsRenderer: new google.maps.DirectionsRenderer()
    },
    mounted () {
    	this.initMap();
    	this.axs  = axios.create({
            baseURL: 'http://localhost:8080/OpenSource-master_reto'
        });
        this.axs.defaults.headers.post['Content-Type'] = 'application/x-www-formbody-urlencoded';
        this.fetch();
    },
    methods: {
        fetch() {
            this.axs.get('/personas').then(response => {
                    this.personas = response.data
                    this.initSecondMap();
            	}
            ) 
        },
        save(evt) {

            evt.preventDefault();
        	if(this.latitude != null && this.longitude != null) {
        	
            url = '/personas';
            const formdata = new URLSearchParams();
            formdata.append('Nombre', this.formbody.nombre);
            formdata.append('Cedula', this.formbody.cedula);
            formdata.append('FechaNacimiento', this.formbody.fechaNacimiento);
            formdata.append('Latitude', this.latitude);
            formdata.append('Longitude', this.longitude);
            
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
        	} else {
        		alert("Debes elegir un lugar en el mapa");
        	}
        },
        resetForm(evt) {
            evt.preventDefault();
            this.formbody.nombre = '';
            this.formbody.cedula = '';
            this.formbody.fechaNacimiento = '';
            this.current = null;
            this.currentOperation = "Crear";
            this.initMap();
            
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
        },
        initMap() {
          map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: 18.473, lng: -69.913},
            zoom: 14
          });
          
          let that = this;
          
          map.addListener('click', function(e) {

	       	   if (that.markersArray.length == 0) {
	       		that.addMarker(e.latLng);
	       	   }
            });
        },
        addMarker(latLng) {
  	   
          let marker = new google.maps.Marker({
              map: map,
              position: latLng,
              draggable: true
          });
          
          this.latitude = latLng.lat();
          this.longitude = latLng.lng();
                    
          let that = this;
          marker.addListener('dragend', function() {
              let pos = marker.getPosition();
              that.latitude = pos.lat();
              that.longitude = pos.lng();
              map.setCenter(pos);
            });
          this.markersArray.push(marker);
        },
        onClick(e) {
            if (e.target.classList.contains('info-btn')) {
            	
            	var id = e.target.id;
            	var marker = this.markersSecondMapArray.filter(v => v.getTitle() == id)[0];
          	    marker.clickable = false;
            	var infoWindow = this.markersInfoWindows.filter(c => c.Id == id)[0].InfoWindow;
            	infoWindow.close();
        		var pos = marker.getPosition();
          	    
            	if (this.currentClicked != null) {
            		
            		 var request = {
            				    origin: new google.maps.LatLng(this.currentClicked.Latitude, this.currentClicked.Longitude),
            				    destination: new google.maps.LatLng(pos.lat(), pos.lng()),
            				    travelMode: 'DRIVING'
            				  };
            		 

				      this.directionsRenderer = new google.maps.DirectionsRenderer();
			          this.directionsRenderer.setMap(this.secondMap);

	            		 let that = this;
            				  this.directionsService.route(request, function(result, status) {
            				    if (status == 'OK') {
            				      that.directionsRenderer.setDirections(result);
            				      that.setMarkerAnimation(null);
            				    }
            				  });
            		this.currentClicked = null;
            	} else {
            		this.currentClicked =  { "Latitude": pos.lat(), "Longitude": pos.lng() };
                	for(x = 0; x < this.markersSecondMapArray.length; x++) {
                		if (this.markersSecondMapArray[x].getTitle() != id) {
                			this.markersSecondMapArray[x].setAnimation(google.maps.Animation.BOUNCE);

                			/*for (n = 0; n < this.markersInfoWindows.length; n++) {
                				this.markersInfoWindows[n].InfoWindow.open(this.secondMap, this.markersSecondMapArray[n]);
                			}*/
                			
                		}z
                	} 
                	alert("Elige otro marcador a donde ir");
	                this.directionsRenderer.setMap(null);
            	}
            }
          },
          setMarkerAnimation(value) {
        	  for (o=0; o < this.markersSecondMapArray.length; o++) {
        		  this.markersSecondMapArray[o].setAnimation(value);
        	  }
          }
          ,
        initSecondMap() {
        	 this.secondMap = new google.maps.Map(document.getElementById('map2'), {
                 center: {lat: 18.473, lng: -69.913},
                 zoom: 14
               });
        	         	                
               let that = this;
               
               for (x=0; x < this.personas.length; x++) {
            	   if (this.personas[x].Latitude != "") {
            		
            		             		   
	            	   let marker = new google.maps.Marker({
		                   map: this.secondMap,
		                   position: new google.maps.LatLng(this.personas[x].Latitude, this.personas[x].Longitude),
		                   title: this.personas[x].Nombre
		               });
	            	   
	            	   this.markersSecondMapArray.push(marker);
	            	   
	            	   var contentString = '<div id="content">'+
         		      marker.getTitle() +
         		      '<br/>' +
         		      '<br/>' +
         		      '<button class="btn btn-primary info-btn" id="'+marker.getTitle()+'">Ir</button>' +
         		      '</div>';

	                   
	            		  var infowindow = new google.maps.InfoWindow({
	            		    content: contentString
	            		  });
	            	   
	            	   this.markersInfoWindows.push({ "Id": marker.getTitle(), "InfoWindow": infowindow });
	                         
		               marker.addListener('click', function() {
		                   let pos = marker.getPosition();
		                   that.latitude = pos.lat();
		                   that.longitude = pos.lng();

			               	var currentInfoWindow = that.markersInfoWindows.filter(c => c.Id == marker.getTitle())[0].InfoWindow;
			               	currentInfoWindow.open(that.secondMap, marker);
		                 });
		               }
               }
        },
        
    }
});