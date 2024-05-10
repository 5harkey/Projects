from flask import Flask, flash, render_template, request, redirect, url_for
from DataAccessFile import DataAccess
from Product import Device
from datetime import datetime

app = Flask(__name__)
app.secret_key = 'f9uinu9n3948nf983nv9ewsunvg34q90a8mfno'
data_access = DataAccess()

@app.route('/')
def index():
    #Display all devices initially
    devices = sorted(data_access.load(), key=lambda x: x.id)
    return render_template('inventory.html', devices=devices)

@app.route('/search', methods=['POST'])
def search():
    #Display devices whos name contains the search_query
    search_query = request.form['search_query']
    if search_query:
        devices = data_access.search(search_query)
    else:
        devices = data_access.load()
    return render_template('inventory.html', devices=devices)

@app.route('/add', methods=['POST'])
def new_device():
    if request.method == 'POST':
        #Check if any fields are empty
        if not request.form['id'] or not request.form['name'] or not request.form['model'] or not request.form['serial_number'] or not request.form['price'] or not request.form['manufacture_date']:
            flash('All fields are required.')
            return redirect(url_for('new_device_form'))
        #Check price is a valid number
        try:
            price = float(request.form['price'])
        except ValueError:
            flash('Price must be a valid number.')
            return redirect(url_for('new_device_form'))
        #Check manufacture_date is in the correct format
        try:
            manufacture_date = datetime.strptime(request.form['manufacture_date'], '%m-%d-%Y')
        except ValueError:
            flash('Manufacture date must be in the format MM-DD-YYYY.')
            return redirect(url_for('new_device_form'))
        #Add the new device
        id = request.form['id']
        name = request.form['name']
        model = request.form['model']
        serial_number = request.form['serial_number']
        new_device = Device(int(id), name, model, serial_number, price, manufacture_date)
        data_access.add(new_device)
        return redirect(url_for('index')) 
    
@app.route('/addForm')
def new_device_form():
    next_id = 1
    #Logic to get the next ID
    for device in data_access.load():
        if device.id == next_id:
            next_id += 1
        else:
            break
    device = Device(next_id, '', '', '', 0.0, datetime.strptime('12-31-9999','%m-%d-%Y'))
    return render_template('add_device.html', device=device)

@app.route('/edit', methods=['POST'])
def edit_device_post():
    id = request.form['id']
    name = request.form['name']
    model = request.form['model']
    serial_number = request.form['serial_number']
    price = request.form['price']
    manufacture_date = request.form['manufacture_date']
    
    #Check if any form are empty
    if not request.form['id'] or not request.form['name'] or not request.form['model'] or not request.form['serial_number'] or not request.form['price'] or not request.form['manufacture_date']:
        flash('All fields are required.')
        return redirect(url_for('edit_device_form', id=id))
    #Check price is a valid number
    try:
        price = float(price)
    except ValueError:
        flash('Price must be a valid number.')
        return redirect(url_for('edit_device_form', id=id))
    #Check manufacture_date is in the correct format
    try:
        manufacture_date = datetime.strptime(manufacture_date, '%m-%d-%Y')
    except ValueError:
        flash('Manufacture date must be in the format MM-DD-YYYY.')
        return redirect(url_for('edit_device_form', id=id))
    #Update the device
    updated_device = Device(int(id), name, model, serial_number, price, manufacture_date)
    data_access.update(updated_device)
    return redirect(url_for('index'))

@app.route('/editForm/<int:id>', methods=['GET'])   
def edit_device_form(id):
    device = data_access.search_id(id)
    return render_template('edit_device.html', device=device)

@app.route('/delete/<int:id>', methods=['GET'])
def confirm_delete_device(id):
    #Delete confirm popup
    device = data_access.search_id(id)
    return render_template('confirm_delete.html', device=device)

@app.route('/deleteConfirmed/<int:id>', methods=['GET'])
def delete_device(id):
    #Delete the device after confirmation
    data_access.delete(id)
    return redirect(url_for('index'))


if __name__ == '__main__':
    app.run(debug=True)
