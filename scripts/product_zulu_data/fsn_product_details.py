#!/usr/bin/python
import csv;
import MySQLdb;
import requests;
import json;
from signal import signal, SIGPIPE, SIG_DFL
import time;
import socket;
import sys;
import os;

RP_DB_NAME='erp_inv_db'
RP_DB_HOST='10.32.173.190'
RP_DB_USER='ip_sys_rw'
RP_DB_PASS='caiP5yaa'

TEST_DB_NAME='erp_inv_db_test'
TEST_DB_HOST='10.32.225.159'
TEST_DB_USER='ip_sys_rw'
TEST_DB_PASS='caiP5yaa'

def get_fsn_list ():

    conn = MySQLdb.connect(RP_DB_HOST, RP_DB_USER, RP_DB_PASS, RP_DB_NAME, local_infile=1);
    query = "select distinct(fsn) from ip_group_fsns inner join ip_groups on ip_group_fsns.group_id = ip_groups.id"
    cur = conn.cursor()
    cur.execute(query);
    result=[]
    row = cur.fetchone()
    while row is not None:
        result.append(row[0]);
        row = cur.fetchone()
    conn.close();
    return result;

def insert_product_data ():
    conn = MySQLdb.connect(RP_DB_HOST, RP_DB_USER, RP_DB_PASS, RP_DB_NAME, local_infile=1);
    cur = conn.cursor();
    query = "LOAD DATA LOCAL INFILE 'fsn_product_data.csv' REPLACE INTO TABLE product_detail FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n' (@fsn, @vertical, @category, @super_category, @title, @brand, @fsp) SET fsn = @fsn, vertical = @vertical, category = @category, super_category = @super_category, title = @title, brand = @brand, fsp = @fsp, created_at = NOW();"
    cur.execute(query);
    conn.commit();
    print "table updated";
    conn.close();


def get_fsn_product_data (fsn_list):
    headers = {
        'z-requestId' : 'req-id',
        'z-clientId' : 'retail.rp',
        'z-timestamp' : 'Time.now.to_i'
    }
    size = 0;
    entityIds = ''
    iter = 0;
    fsn_vertical_mapping = {}
    no_of_fsn = len(fsn_list)

    while (1):

        if(iter >= no_of_fsn):
            break;

        batch_size = 30
        sliced_list = fsn_list[iter: iter+batch_size]
        iter += batch_size;

        fsn_sliced_list_count = len(sliced_list)
        print iter;

        for index in range (0, fsn_sliced_list_count):
            if(index == fsn_sliced_list_count - 1) :
                entityIds += str(sliced_list[index])
            else :
                entityIds += str(sliced_list[index]) + ','

        url = 'http://10.47.1.8:31200/views?viewNames=retail_product_attributes&entityIds=';
        url += entityIds
        #print url;

        try:
            response = requests.get(url=url,headers=headers)
            if(response.status_code == 200):
                response = json.loads(response.text.encode('ascii', 'ignore'));
            else:
                print "api call returned " + str(response.status_code)
                entityIds = ''
                continue;

            with open('fsn_product_data.csv', 'a') as fp:
                a = csv.writer(fp, delimiter=',');
                data = []
                iterator = 0
                entityViews = response['entityViews'];
                entityId = ''
                vertical = ''
                category = ''
                super_category = ''
                title = ''
                brand = ''
                for entityView in entityViews:
                    if 'entityId' in entityView:
                        entityId = entityView['entityId']

                        if 'view' in entityView:
                            view = entityView['view']

                            if 'analytics_info' in view:
                                analytics_info = view['analytics_info']

                                if 'vertical' in analytics_info:
                                    vertical = analytics_info['vertical']

                                if 'category' in analytics_info:
                                    category = analytics_info['category']

                                if 'super_category' in analytics_info:
                                    super_category = analytics_info['super_category']

                            if 'title' in view:
                                title = view['title']
                                if title != None:
                                    title = title.encode('utf-8')

                            if 'supply_chain' in view:
                                supply_chain = view['supply_chain']
                                supply_chain = json.loads(supply_chain)

                                if 'product_attributes' in supply_chain:
                                    product_attributes = supply_chain['product_attributes']

                                    if 'flipkart_selling_price' in product_attributes:
                                        fsp = product_attributes['flipkart_selling_price']

                                    if vertical == 'book':
                                        if 'title' in product_attributes:
                                            title = product_attributes['title']

                            if 'brand' in view:
                                brand = view['brand']
                    else :
                        entityId = ''

                    if (entityId != ''):
                        data.append([entityId, vertical, category, super_category, title, brand, fsp])
                a.writerows(data);

        except socket.error, e:
            print "socket error"
        except IOError, e:
            print "io error"

        entityIds = ''

    return fsn_vertical_mapping

def main():
    fsn_list = get_fsn_list();
    print "fsn_list fetched";
    print len(fsn_list)
    get_fsn_product_data(fsn_list);
    print "fsn_product_data fetched";
    insert_product_data()
    print 'done';
    os.remove("fsn_product_data.csv");
    print 'csv removed';

if __name__ == "__main__":
    main();
