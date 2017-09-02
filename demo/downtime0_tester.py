#! /usr/bin/env python

import argparse
import requests
import time

#
# Author: Leandro Beretta <lberetta@redhat.com>
#
# Script to test Downtime 0 in deployments with OpenShift
#


def create_arg_parser():
    """ Creates the argument parser to user. """

    custom_parser = argparse.ArgumentParser(description='Test Downtime 0 with OpenShift')

    custom_parser.add_argument('url',
                               metavar='url',
                               type=str,
                               help='The URL to test the service (GET Method used)')

    custom_parser.add_argument('interval',
                               metavar='interval',
                               type=float,
                               help='The interval to test the service (in seconds)')

    return custom_parser


if __name__ == '__main__':
    parser = create_arg_parser()
    args = parser.parse_args()

    try:
        while True:
            response = requests.get(args.url)
            body = response.json()

            print('Status Code: {} - Version: {}'.format(response.status_code, body['version']))

            time.sleep(args.interval)
    except KeyboardInterrupt:
        quit(0)